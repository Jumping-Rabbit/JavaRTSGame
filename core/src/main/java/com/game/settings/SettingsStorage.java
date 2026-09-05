package com.game.settings;

import com.game.utils.LoggerUtil;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import java.io.File;

public class SettingsStorage {
    protected static void writeSettings(String directory, String key, Object value) {
        var objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File("core/resources/settings.json"));
        JsonNode setting = root.path(directory);
        ObjectNode objectNode = (ObjectNode) setting;
        switch (value.getClass().getSimpleName()) {
            case "String" -> objectNode.put(key, value.toString());
            case "Integer" -> objectNode.put(key, Integer.parseInt(value.toString()));
            case "Boolean" -> objectNode.put(key, Boolean.parseBoolean(value.toString()));
        }
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("core/resources/settings.json"), root);
        } catch (Exception e) {
            LoggerUtil.log(e);
        }
    }
}
