package com.game.lwjgl.vulkan;


import com.game.lwjgl.shaderc.Shaders;
import org.lwjgl.vulkan.VK14;

public enum PipelineTypes {
  TWO_D(VK14.VK_FORMAT_R8G8B8A8_SRGB, VK14.VK_FORMAT_UNDEFINED, "2d", Shaders.VERT2D, Shaders.FRAG2D);
  private final int colorFormat;
  private final int depthFormat;
  private final String shaderName;
  private final Shaders vert;
  private final Shaders frag;
  
  PipelineTypes(int colorFormat, int depthFormat, String shaderName, Shaders vert, Shaders frag) {
    this.colorFormat = colorFormat;
    this.depthFormat = depthFormat;
    this.shaderName = shaderName;
    this.vert = vert;
    this.frag = frag;
  }
  
  int getColorFormat() {
    return colorFormat;
  }
  
  int getDepthFormat() {
    return depthFormat;
  }
  
  String getShaderName() {
    return shaderName;
  }
  
  Shaders getFrag() {
    return frag;
  }
  
  Shaders getVert() {
    return vert;
  }
}
