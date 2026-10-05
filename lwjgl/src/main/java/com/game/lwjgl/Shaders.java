package com.game.lwjgl;

import org.lwjgl.util.shaderc.Shaderc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

enum Shaders {
  FRAG2D("2d.frag", Shaderc.shaderc_fragment_shader),
  VERT2D("2d.vert", Shaderc.shaderc_vertex_shader),
  COLLISION("collision.comp", Shaderc.shaderc_compute_shader);
  
  private final String name;
  private final int type;
  
  Shaders(String name, int type) {
    this.name = name;
    this.type = type;
  }
  
  static Shaders fromValue(String value) {
    for (Shaders shader : values()) {
      if (shader.name.equalsIgnoreCase(value)) {
        return shader;
      }
    }
    throw new IllegalArgumentException("Unknown shader file: " + value);
  }
  
  static String readShader(Shaders shader) throws IOException {
    String path = "/shaders/" + shader.name;
    try (InputStream in = LwjglManager.class.getResourceAsStream(path)) {
      if (in == null) throw new FileNotFoundException("Missing shader resource: " + path);
      return new String(in.readAllBytes(), StandardCharsets.UTF_8);
    }
  }
  
  String getName() {
    return name;
  }
  
  public int getType() {
    return type;
  }
}
