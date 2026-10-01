package com.game.utils;

import com.game.Init;
import com.game.screens.LoadingScreen;
import tools.jackson.databind.ObjectMapper;

@Init(stage = 0)
public class JsonUtil {
  public static ObjectMapper objectMapper;
  
  public static void init(LoadingScreen loadingScreen){
    objectMapper = new ObjectMapper();
    loadingScreen.increment();
  }
}
