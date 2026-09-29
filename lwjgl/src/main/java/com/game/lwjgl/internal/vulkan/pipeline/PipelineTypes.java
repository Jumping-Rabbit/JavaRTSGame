package com.game.lwjgl.internal.vulkan.pipeline;

import org.lwjgl.vulkan.VK14;

public enum PipelineTypes {
  UI(VK14.VK_FORMAT_R8G8B8A8_SRGB, VK14.VK_FORMAT_UNDEFINED, "ui");
  private final int colorFormat;
  private final int depthFormat;
  private final String shaderName;
  
  PipelineTypes(int colorFormat, int depthFormat, String shaderName) {
    this.colorFormat = colorFormat;
    this.depthFormat = depthFormat;
    this.shaderName = shaderName;
  }
  
  public int getColorFormat() {
    return colorFormat;
  }
  public int getDepthFormat() {
    return depthFormat;
  }
  public String getShaderName() {
    return shaderName;
  }
}
