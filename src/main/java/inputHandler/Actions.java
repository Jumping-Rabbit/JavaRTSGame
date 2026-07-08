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
    NONE("none"),
    SET_CONTROL_GROUP_1("set control group 1"),
    SET_CONTROL_GROUP_2("set control group 2"),
    SET_CONTROL_GROUP_3("set control group 3"),
    SET_CONTROL_GROUP_4("set control group 4"),
    SET_CONTROL_GROUP_5("set control group 5"),
    SET_CONTROL_GROUP_6("set control group 6"),
    SET_CONTROL_GROUP_7("set control group 7"),
    SET_CONTROL_GROUP_8("set control group 8"),
    SET_CONTROL_GROUP_9("set control group 9"),
    SET_CONTROL_GROUP_10("set control group 10"),
    SET_CONTROL_GROUP_11("set control group 11"),
    SET_CONTROL_GROUP_12("set control group 12"),
    SET_CONTROL_GROUP_13("set control group 13"),
    SET_CONTROL_GROUP_14("set control group 14"),
    SET_CONTROL_GROUP_15("set control group 15"),
    SET_CONTROL_GROUP_16("set control group 16"),
    SET_CONTROL_GROUP_17("set control group 17"),
    SET_CONTROL_GROUP_18("set control group 18"),
    SET_CONTROL_GROUP_19("set control group 19"),
    SET_CONTROL_GROUP_20("set control group 20"),
    GET_CONTROL_GROUP_1("set control group 1"),
    GET_CONTROL_GROUP_2("get control group 2"),
    GET_CONTROL_GROUP_3("get control group 3"),
    GET_CONTROL_GROUP_4("get control group 4"),
    GET_CONTROL_GROUP_5("get control group 5"),
    GET_CONTROL_GROUP_6("get control group 6"),
    GET_CONTROL_GROUP_7("get control group 7"),
    GET_CONTROL_GROUP_8("get control group 8"),
    GET_CONTROL_GROUP_9("get control group 9"),
    GET_CONTROL_GROUP_10("get control group 10"),
    GET_CONTROL_GROUP_11("get control group 11"),
    GET_CONTROL_GROUP_12("get control group 12"),
    GET_CONTROL_GROUP_13("get control group 13"),
    GET_CONTROL_GROUP_14("get control group 14"),
    GET_CONTROL_GROUP_15("get control group 15"),
    GET_CONTROL_GROUP_16("get control group 16"),
    GET_CONTROL_GROUP_17("get control group 17"),
    GET_CONTROL_GROUP_18("get control group 18"),
    GET_CONTROL_GROUP_19("get control group 19"),
    GET_CONTROL_GROUP_20("get control group 20"),
    ADD_CONTROL_GROUP_1("add control group 1"),
    ADD_CONTROL_GROUP_2("add control group 2"),
    ADD_CONTROL_GROUP_3("add control group 3"),
    ADD_CONTROL_GROUP_4("add control group 4"),
    ADD_CONTROL_GROUP_5("add control group 5"),
    ADD_CONTROL_GROUP_6("add control group 6"),
    ADD_CONTROL_GROUP_7("add control group 7"),
    ADD_CONTROL_GROUP_8("add control group 8"),
    ADD_CONTROL_GROUP_9("add control group 9"),
    ADD_CONTROL_GROUP_10("add control group 10"),
    ADD_CONTROL_GROUP_11("add control group 11"),
    ADD_CONTROL_GROUP_12("add control group 12"),
    ADD_CONTROL_GROUP_13("add control group 13"),
    ADD_CONTROL_GROUP_14("add control group 14"),
    ADD_CONTROL_GROUP_15("add control group 15"),
    ADD_CONTROL_GROUP_16("add control group 16"),
    ADD_CONTROL_GROUP_17("add control group 17"),
    ADD_CONTROL_GROUP_18("add control group 18"),
    ADD_CONTROL_GROUP_19("add control group 19"),
    ADD_CONTROL_GROUP_20("add control group 20");


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
