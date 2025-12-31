package de.gamedude.evt.automation;

import java.util.HashMap;
import java.util.Map;

public class StateRegistry {

    private static final Map<String, StateFactory> registry = new HashMap<>();

    public static void registerState(String CMD, StateFactory factory) {
        registry.put(CMD.toUpperCase(), factory);
    }
    public static StateFactory get(String cmd) {
        return registry.get(cmd.toUpperCase());
    }

}
