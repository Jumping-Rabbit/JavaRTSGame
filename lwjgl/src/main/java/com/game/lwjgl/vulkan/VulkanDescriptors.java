package com.game.lwjgl.vulkan;

import com.game.lwjgl.LwjglData;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK14;
import org.lwjgl.vulkan.VkDescriptorImageInfo;
import org.lwjgl.vulkan.VkDescriptorPoolCreateInfo;
import org.lwjgl.vulkan.VkDescriptorPoolSize;
import org.lwjgl.vulkan.VkDescriptorSetAllocateInfo;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkWriteDescriptorSet;

import java.nio.LongBuffer;

public class VulkanDescriptors {
  private VulkanDescriptors() {
  }
  
  public static long createCollisionDescriptorSetLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(4, stack);
      
      // binding 0: uniform buffer
      bindings.get(0)
          .binding(0)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 1: entities
      bindings.get(1)
          .binding(1)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 2: cell starts
      bindings.get(2)
          .binding(2)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      // binding 3: output
      bindings.get(3)
          .binding(3)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_COMPUTE_BIT);
      
      VkDescriptorSetLayoutCreateInfo layoutCreateInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack)
          .sType(VK14.VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO)
          .pBindings(bindings);
      
      long[] pDescriptorSetLayout = new long[1];
      if (VK14.vkCreateDescriptorSetLayout(lwjglData.vkDevice, layoutCreateInfo, null, pDescriptorSetLayout) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create collision compute descriptor set layout");
      }
      
      return pDescriptorSetLayout[0];
    }
  }
  
  public static long create2DDescriptorSetLayout(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkDescriptorSetLayoutBinding.Buffer buffer = VkDescriptorSetLayoutBinding.calloc(1, stack);
      buffer.get(0).binding(0)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
          .descriptorCount(1)
          .stageFlags(VK14.VK_SHADER_STAGE_FRAGMENT_BIT);
      VkDescriptorSetLayoutCreateInfo info = VkDescriptorSetLayoutCreateInfo.calloc(stack)
          .sType$Default().pBindings(buffer);
      long[] out = new long[1];
      if (VK14.vkCreateDescriptorSetLayout(lwjglData.vkDevice, info, null, out) != VK14.VK_SUCCESS)
        throw new RuntimeException("failed to create 2D descriptor set layout");
      return out[0];
    }
  }
  
  public static void makeDescriptorSet(LwjglData lwjglData) {
    try (MemoryStack stack = MemoryStack.stackPush()) {
      VkDescriptorPoolSize.Buffer poolSize = VkDescriptorPoolSize.calloc(1, stack)
          .type(VK14.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
          .descriptorCount(1);
      VkDescriptorPoolCreateInfo poolInfo = VkDescriptorPoolCreateInfo.calloc(stack)
          .sType$Default()
          .pPoolSizes(poolSize)
          .maxSets(1);
      LongBuffer pPool = stack.mallocLong(1);
      if (VK14.vkCreateDescriptorPool(lwjglData.vkDevice, poolInfo, null, pPool) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to create descriptor pool");
      }
      lwjglData.descriptorPool = pPool.get(0);
      
      VkDescriptorSetAllocateInfo allocInfo = VkDescriptorSetAllocateInfo.calloc(stack)
          .sType$Default()
          .descriptorPool(lwjglData.descriptorPool)
          .pSetLayouts(stack.longs(lwjglData.descriptorSetLayout2D));
      LongBuffer pSet = stack.mallocLong(1);
      if (VK14.vkAllocateDescriptorSets(lwjglData.vkDevice, allocInfo, pSet) != VK14.VK_SUCCESS) {
        throw new RuntimeException("failed to allocate descriptor set");
      }
      lwjglData.descriptorSet = pSet.get(0);
      
      VkDescriptorImageInfo.Buffer imageInfo = VkDescriptorImageInfo.calloc(1, stack)
          .imageLayout(VK14.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
          .imageView(lwjglData.fontImageView)
          .sampler(lwjglData.fontSampler);
      VkWriteDescriptorSet.Buffer write = VkWriteDescriptorSet.calloc(1, stack)
          .sType$Default()
          .dstSet(lwjglData.descriptorSet)
          .dstBinding(0)
          .descriptorType(VK14.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
          .descriptorCount(1)
          .pImageInfo(imageInfo);
      VK14.vkUpdateDescriptorSets(lwjglData.vkDevice, write, null);
    }
  }
  
  public static void cleanupDescriptors(LwjglData lwjglData) {
    VK14.vkDestroyDescriptorPool(lwjglData.vkDevice, lwjglData.descriptorPool, null);
    VK14.vkDestroyDescriptorSetLayout(lwjglData.vkDevice, lwjglData.descriptorSetLayout2D, null);
    VK14.vkDestroyDescriptorSetLayout(lwjglData.vkDevice, lwjglData.descriptorSetLayoutCollision, null);
  }
}
