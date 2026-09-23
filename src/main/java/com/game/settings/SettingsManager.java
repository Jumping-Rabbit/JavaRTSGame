package com.game.settings;

import com.game.Init;
import com.game.screens.LoadingScreen;
import com.game.settings.settingsEnums.DisplayModes;
import com.game.settings.settingsEnums.GraphicsQuality;
import com.game.settings.settingsEnums.SettingsEnums;
import com.game.utils.LoggerUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;

@Init(stage = 1)
public class SettingsManager {
  
  private static Object2ObjectOpenHashMap<SettingSections, Object2ObjectOpenHashMap<Settings, Setting<?>>> settingMap;
  
  public static void init(LoadingScreen loadingScreen) {
    settingMap = new Object2ObjectOpenHashMap<>();
    addGraphicsSettings();
    addAudioSettings();
    loadSettings();
    loadingScreen.increment();
  }
  
  private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting, Class<? extends Enum<?>> value) {
    settingsSectionMap.put(setting, new EnumSetting(setting.getId(), DisplayModes.class));
  }
  
  private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting, Integer min, Integer max) {
    settingsSectionMap.put(setting, new IntSetting(setting.getId(), min, max));
  }
  
  private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting) {
    settingsSectionMap.put(setting, new BoolSetting(setting.getId()));
  }
  
  private static void addGraphicsSettings() {
    Object2ObjectOpenHashMap<Settings, Setting<?>> settingsGraphicsMap = new Object2ObjectOpenHashMap<>();
    newSetting(settingsGraphicsMap, Settings.DISPLAY_MODES, DisplayModes.class);
    newSetting(settingsGraphicsMap, Settings.GRAPHICS_QUALITY, GraphicsQuality.class);
    newSetting(settingsGraphicsMap, Settings.MONITOR_NUM, 0, null);
    newSetting(settingsGraphicsMap, Settings.ANTIALIASING);
    newSetting(settingsGraphicsMap, Settings.TARGET_FPS, 0, null);
    
    settingMap.put(SettingSections.GRAPHICS, settingsGraphicsMap);
  }
  
  private static void addAudioSettings() {
    Object2ObjectOpenHashMap<Settings, Setting<?>> settingsAudioMap = new Object2ObjectOpenHashMap<>();
    newSetting(settingsAudioMap, Settings.MASTER_VOLUME, 0, 100);
    newSetting(settingsAudioMap, Settings.BGM_VOLUME, 0, 100);
    newSetting(settingsAudioMap, Settings.SFX_VOLUME, 0, 100);
    
    settingMap.put(SettingSections.GRAPHICS, settingsAudioMap);
  }
  
  public static void loadSettings() {
    ObjectMapper objectMapper = new ObjectMapper();
    File file = new File("resources/settings.json");
    
    if (!file.exists()) {
      return;
    }
    
    try {
      JsonNode root = objectMapper.readTree(file);
      
      for (var sectionEntry : settingMap.object2ObjectEntrySet()) {
        String sectionName = sectionEntry.getKey().name();
        JsonNode sectionNode = getChildCaseInsensitive(root, sectionName);
        
        if (sectionNode != null) {
          var innerMap = sectionEntry.getValue();
          
          for (var settingEntry : innerMap.object2ObjectEntrySet()) {
            Settings settingKey = settingEntry.getKey();
            Setting<?> settingObj = settingEntry.getValue();
            
            JsonNode valueNode = getChildCaseInsensitive(sectionNode, settingKey.getId());
            
            if (valueNode != null) {
              if (settingObj instanceof IntSetting intSetting && valueNode.isInt()) {
                intSetting.setValue(valueNode.asInt());
              } else if (settingObj instanceof BoolSetting boolSetting && valueNode.isBoolean()) {
                boolSetting.setValue(valueNode.asBoolean());
              } else if (settingObj instanceof EnumSetting enumSetting && valueNode.isTextual()) {
                String enumStringValue = valueNode.asString();
                
                Class enumClass = enumSetting.getEnumClass();
                
                var enumValue = SettingsEnums.fromValue(enumClass, enumStringValue);
                if (enumValue != null) {//the warnings probably fine righttt
                  enumSetting.setValue(enumValue);
                }
              }
            }
          }
        }
      }
    } catch (JacksonException e) {
      LoggerUtil.log(e);
    }
  }
  
  private static JsonNode getChildCaseInsensitive(JsonNode parent, String keyName) {
    if (parent == null || !parent.isObject()) {
      return null;
    }
    
    for (String name : parent.propertyNames()) {
      if (name.equalsIgnoreCase(keyName)) {
        return parent.get(name);
      }
    }
    return null;
  }
  
  private static void writeSetting(Settings setting) {
    File file = new File("resources/settings.json");
    ObjectMapper objectMapper = new ObjectMapper();
    ObjectNode root;
    
    try {
      if (file.exists()) {
        root = (ObjectNode) objectMapper.readTree(file);
      } else {
        root = objectMapper.createObjectNode();
      }
      
      for (var sectionEntry : settingMap.object2ObjectEntrySet()) {
        String sectionName = sectionEntry.getKey().name().toLowerCase();
        var innerMap = sectionEntry.getValue();
        
        if (innerMap.containsKey(setting)) {
          Setting<?> settingObj = innerMap.get(setting);
          
          ObjectNode sectionNode;
          JsonNode existingSection = getChildCaseInsensitive(root, sectionName);
          if (existingSection instanceof ObjectNode existingObjectNode) {
            sectionNode = existingObjectNode;
          } else {
            sectionNode = root.putObject(sectionName);
          }
          
          String keyId = setting.getId();
          if (settingObj instanceof IntSetting intSetting) {
            sectionNode.put(keyId, intSetting.getValue());
          } else if (settingObj instanceof BoolSetting boolSetting) {
            sectionNode.put(keyId, boolSetting.getValue());
          } else if (settingObj instanceof EnumSetting enumSetting) {
            Object enumVal = enumSetting.getValue();
            if (enumVal instanceof SettingsEnums settingsEnum) {
              sectionNode.put(keyId, settingsEnum.getString());
            } else if (enumVal != null) {
              sectionNode.put(keyId, enumVal.toString().toLowerCase());
            }
          }
          
          break;
        }
      }
      
      objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, root);
      
    } catch (Exception e) {
      LoggerUtil.log(e);
    }
  }
  
  public String getSettingStringValue(Settings setting) {
    return switch (setting) {
      case TARGET_FPS -> settingMap.get(SettingSections.GRAPHICS).get(Settings.TARGET_FPS).getValue().toString();
      case MONITOR_NUM -> settingMap.get(SettingSections.GRAPHICS).get(Settings.MONITOR_NUM).toString();
      case DISPLAY_MODES -> settingMap.get(SettingSections.GRAPHICS).get(Settings.DISPLAY_MODES).toString();
      case MASTER_VOLUME -> settingMap.get(SettingSections.AUDIO).get(Settings.MASTER_VOLUME).toString();
      case BGM_VOLUME -> settingMap.get(SettingSections.AUDIO).get(Settings.BGM_VOLUME).toString();
      case SFX_VOLUME -> settingMap.get(SettingSections.AUDIO).get(Settings.SFX_VOLUME).toString();
      case ANTIALIASING -> settingMap.get(SettingSections.GRAPHICS).get(Settings.ANTIALIASING).toString();
      case GRAPHICS_QUALITY -> settingMap.get(SettingSections.GRAPHICS).get(Settings.GRAPHICS_QUALITY).toString();
    };
  }
  
  @SuppressWarnings("unchecked")
  public void setSetting(Settings setting, String set) {
    switch (setting.getSettingType()) {
      case INTEGER: {
        setSettingValue((Setting<Integer>) getSetting(setting), Integer.parseInt(set));
        break;
      }
      case BOOLEAN: {
        setSettingValue((Setting<Boolean>) getSetting(setting), Boolean.parseBoolean(set));
        break;
      }
    }
    switch (setting) {
      case TARGET_FPS: {
        SettingsApplicator.setFPS((int) getSetting(setting).getValue());
        break;
      }
      case MONITOR_NUM: {
        SettingsApplicator.setMonitorNum((int) getSetting(setting).getValue());
        break;
      }
      case DISPLAY_MODES: {
        setSettingValue((Setting<Enum<DisplayModes>>) getSetting(setting), DisplayModes.fromValue(set));
        SettingsApplicator.setDisplayModes((EnumSetting) getSetting(setting).getValue());
        break;
      }
      case MASTER_VOLUME: {
        SettingsApplicator.setMasterVolume((int) getSetting(setting).getValue());
        break;
      }
      case BGM_VOLUME: {
        SettingsApplicator.setBGMVolume((int) getSetting(setting).getValue());
        break;
      }
      case SFX_VOLUME: {
        SettingsApplicator.setSFXVolume((int) getSetting(setting).getValue());
        break;
      }
      case ANTIALIASING: {
        SettingsApplicator.setAntialiasing((boolean) getSetting(setting).getValue());
        break;
      }
      case GRAPHICS_QUALITY: {
        setSettingValue((Setting<Enum<GraphicsQuality>>) getSetting(setting), GraphicsQuality.fromValue(set));
        SettingsApplicator.setGraphicsQuality((EnumSetting) getSetting(setting).getValue());
        break;
      }
    }
  }
  
  public static Setting<?> getSetting(Settings setting) {
    return settingMap.get(setting.getSettingSections()).get(setting);
  }
  
  public static <T> void setSettingValue(Setting<T> setting, T value) {
    setting.setValue(value);
  }
  
}
