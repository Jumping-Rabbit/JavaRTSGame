package com.game.settings;

import com.game.entity.Init;
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

@Init
public class SettingsManager {

    private static Object2ObjectOpenHashMap<SettingSections, Object2ObjectOpenHashMap<Settings, Setting<?>>> settingMap;

    public static void init() {
        settingMap = new Object2ObjectOpenHashMap<>();
        addGraphicsSettings();
        addAudioSettings();
        loadSettings();
    }

    private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting, Class<? extends Enum<?>> value){
        settingsSectionMap.put(setting, new EnumSetting(setting.getId(), DisplayModes.class));
    }
    private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting, Integer min, Integer max){
        settingsSectionMap.put(setting, new IntSetting(setting.getId(), min, max));
    }
    private static void newSetting(Object2ObjectOpenHashMap<Settings, Setting<?>> settingsSectionMap, Settings setting){
        settingsSectionMap.put(setting, new BoolSetting(setting.getId()));
    }
    private static void addGraphicsSettings(){
        Object2ObjectOpenHashMap<Settings, Setting<?>> settingsGraphicsMap = new Object2ObjectOpenHashMap<>();
        newSetting(settingsGraphicsMap, Settings.DISPLAY_MODES, DisplayModes.class);
        newSetting(settingsGraphicsMap, Settings.GRAPHICS_QUALITY, GraphicsQuality.class);
        newSetting(settingsGraphicsMap, Settings.MONITOR_NUM, 0, null);
        newSetting(settingsGraphicsMap, Settings.ANTIALIASING);
        newSetting(settingsGraphicsMap, Settings.TARGET_FPS, 0, null);

        settingMap.put(SettingSections.GRAPHICS, settingsGraphicsMap);
    }
    private static void addAudioSettings(){
        Object2ObjectOpenHashMap<Settings, Setting<?>> settingsAudioMap = new Object2ObjectOpenHashMap<>();
        newSetting(settingsAudioMap, Settings.MASTER_VOLUME, 0, 100);
        newSetting(settingsAudioMap, Settings.BGM_VOLUME, 0, 100);
        newSetting(settingsAudioMap, Settings.SFX_VOLUME, 0, 100);

        settingMap.put(SettingSections.GRAPHICS, settingsAudioMap);
    }

    public static void loadSettings() {
        ObjectMapper objectMapper = new ObjectMapper();
        File file = new File("core/resources/settings.json");

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
        File file = new File("core/resources/settings.json");
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

    public String getSettingStringValue(String id) {
//        return switch (id) {
//            case "targetFPS" -> String.valueOf(getTargetFPS());
//            case "monitorNum" -> String.valueOf(getMonitorNum());
//            case "displayMode" -> getDisplayMode().getString();
//            case "masterVolume" -> String.valueOf(getMasterVolume());
//            case "BGMVolume" -> String.valueOf(getBGMVolume());
//            case "SFXVolume" -> String.valueOf(getSFXVolume());
//            case "antialiasing" -> String.valueOf(getAntialiasing());
//            case "graphicsQuality" -> getGraphicsQuality().getString();
//            default -> "";
//        };
        return "";
    }

    public void setSetting(String id, String set) {
//        switch (id) {
//            case "targetFPS" -> setTargetFPS(Integer.parseInt(set));
//            case "monitorNum" -> setMonitorNum(Integer.parseInt(set));
//            case "displayMode" -> setDisplayMode(DisplayModes.fromValue(set));
//            case "masterVolume" -> setMasterVolume(Integer.parseInt(set));
//            case "BGMVolume" -> setBGMVolume(Integer.parseInt(set));
//            case "SFXVolume" -> setSFXVolume(Integer.parseInt(set));
//            case "antialiasing" -> setAntialiasing(Boolean.parseBoolean(set));
//            case "graphicsQuality" -> setGraphicsQuality(GraphicsQuality.fromValue(set));
//        }
    }

}
