package com.game.lwjgl;

enum ComputePipelineTypes {
  COLLISION(Shaders.COLLISION);
  
  private final Shaders shader;
  ComputePipelineTypes(Shaders shader){
    this.shader = shader;
  }
  
  Shaders getShader() {
    return shader;
  }
}
