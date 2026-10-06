package com.game.lwjgl.Vulkan;


import com.game.lwjgl.Shaderc.Shaders;

public enum ComputePipelineTypes {
  COLLISION(Shaders.COLLISION);
  
  private final Shaders shader;
  
  ComputePipelineTypes(Shaders shader) {
    this.shader = shader;
  }
  
  Shaders getShader() {
    return shader;
  }
}
