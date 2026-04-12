package inputHandler;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.ObjectNode;
import utils.LoggerUtil;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public enum Actions {
    RIGHT("right"),
    LEFT("left"),
    UP("up"),
    DOWN("down"),
    CHANGE_BGM("changeBGM"),
    SETTINGS("settings"),
    CONFIRM("confirm"),
    BACK("back"),
    NONE("none");

    private final String string;
    private static Map<Keys, Actions> keyMap = new HashMap<>();

    Actions(String string) {
        this.string = string;
    }

    public static void init(){
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(new File("resources/keyBinds.json"));
        ObjectNode mutableRoot = (ObjectNode) root;
        for (Keys key : Keys.values()){
            if(root.has(key.getString())){
                keyMap.put(key, fromValue(root.get(key.getString()).asString()));
            }else{
                mutableRoot.put(key.getString(), "none");
                keyMap.put(key, NONE);
            }
        }
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("resources/keyBinds.json"), root);
    }

    public static Actions fromValue(String value) {
        for (Actions action : Actions.values()) {
            if (value.equalsIgnoreCase(action.string)) {
                return action;
            }
        }
        return null;
    }

    public static Actions getAction(Keys key){
        return keyMap.get(key);
    }
}
