package com.game.lwjgl.vulkan;


import com.game.lwjgl.shaderc.Shaders;

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
