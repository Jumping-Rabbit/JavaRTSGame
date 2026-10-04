package com.game.core.utils;

import com.game.core.Init;
import com.game.core.screens.LoadingScreen;
import tools.jackson.databind.ObjectMapper;

@Init(stage = 0)
public class JsonUtil {
  public static ObjectMapper objectMapper;
  
  public static void init(LoadingScreen loadingScreen){
    objectMapper = new ObjectMapper();
    loadingScreen.increment();
  }
}
