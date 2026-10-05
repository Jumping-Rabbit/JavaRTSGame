package com.game.lwjgl;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.shaderc.Shaderc;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


class LwjglShaderc {
  
  private LwjglShaderc() {
  }
  
  static void cleanupShaders(LwjglData lwjglData) {
    lwjglData.shadersToLongHandle.forEach((shaderModule, resultHandle) -> {
      if (resultHandle != 0) {
        VK14.vkDestroyShaderModule(lwjglData.vkDevice, resultHandle, null);
      }
    });
    lwjglData.shadersToLongHandle.clear();
  }
  
  static void compileShaders(LwjglData lwjglData) {
    ConcurrentHashMap<Shaders, Long> tempMap = new ConcurrentHashMap<>();
    int cores = StrictMath.max(Runtime.getRuntime().availableProcessors()/2, 1);
    ExecutorService executorService = Executors.newFixedThreadPool(Math.max(cores - 2, 1));
    
    long baseOptions = Shaderc.shaderc_compile_options_initialize();
    Shaderc.shaderc_compile_options_set_optimization_level(baseOptions, Shaderc.shaderc_optimization_level_performance);
    Shaderc.shaderc_compile_options_set_target_env(baseOptions, Shaderc.shaderc_target_env_vulkan, Shaderc.shaderc_env_version_vulkan_1_3);
    
    List<Future<?>> futures = new ArrayList<>();
    for (Shaders shader : Shaders.values()) {
      futures.add(executorService.submit(() -> {
        long compiler = Shaderc.shaderc_compiler_initialize();
        long options = Shaderc.shaderc_compile_options_clone(baseOptions);
        long result;
        try {
          result = Shaderc.shaderc_compile_into_spv(
              compiler,
              Shaders.readShader(shader),
              shader.getType(),
              shader.getName(),
              "main",
              options);
        } catch (IOException e) {
          throw new RuntimeException(e);
        } finally {
          Shaderc.shaderc_compile_options_release(options);
          Shaderc.shaderc_compiler_release(compiler);
        }
        
        if (Shaderc.shaderc_result_get_compilation_status(result) != Shaderc.shaderc_compilation_status_success) {
          String errorLog = Shaderc.shaderc_result_get_error_message(result);
          Shaderc.shaderc_result_release(result);
          throw new RuntimeException("Shader compilation failed for " + shader.getName() + ":\n" + errorLog);
        }
        
        ByteBuffer spirvBytes = Shaderc.shaderc_result_get_bytes(result);
        try (MemoryStack stack = MemoryStack.stackPush()) {
          VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.calloc(stack)
              .sType(VK14.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
              .pCode(spirvBytes);
          LongBuffer pShaderModule = stack.mallocLong(1);
          int r = VK14.vkCreateShaderModule(lwjglData.vkDevice, createInfo, null, pShaderModule);
          if (r != VK14.VK_SUCCESS) {
            throw new RuntimeException("vkCreateShaderModule failed for " + shader.getName() + ": " + r);
          }
          tempMap.put(shader, pShaderModule.get(0));
        } finally {
          Shaderc.shaderc_result_release(result);
        }
      }));
    }
    
    executorService.shutdown();
    try {
      for (Future<?> f : futures) {
        f.get(60, TimeUnit.SECONDS);
      }
    } catch (Exception e) {
      executorService.shutdownNow();
      throw new RuntimeException("Shader compilation failed", e);
    } finally {
      Shaderc.shaderc_compile_options_release(baseOptions);
    }
    
    lwjglData.shadersToLongHandle.putAll(tempMap);
  }
}
