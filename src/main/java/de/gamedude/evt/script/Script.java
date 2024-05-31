package de.gamedude.evt.script;

import de.gamedude.evt.logic.State;
import joptsimple.internal.Strings;

import java.util.*;

public class Script {

    private final Map<ScriptType, List<State>> typeScriptMap;

    private Iterator<State> currentIterator;
    private State currentState = null;


    public Script(Map<ScriptType, List<State>> typeScriptMap) {
        this.typeScriptMap = typeScriptMap;
    }

    public Map<ScriptType, List<State>> getTypeScriptMap() {
        return typeScriptMap;
    }

    public void tickScriptType(ScriptType type) {
        this.currentIterator = typeScriptMap.get(type).iterator();
    }

    public void tick() {
        if (currentIterator == null)
            return;
        if (currentState == null) {
            if (!currentIterator.hasNext()) {
                currentIterator = null;
                return;
            }
            currentState = currentIterator.next();
        }

        if (currentState.run() == 1) {
            if (!currentIterator.hasNext()) {
                currentIterator = null;
                return;
            }
            currentState = currentIterator.next();
        }
    }

    public enum ScriptType {
        INIT,
        REPEAT,
        FOUND;

        private final static ScriptType[] VALUES = values();

        public static ScriptType byFileName(String name) throws Exception {
            for (ScriptType value : VALUES) {
                if (name.toLowerCase().contains(value.name().toLowerCase()))
                    return value;
            }
            throw new Exception("'" + name + "' is not a suitable name for a type of script.");
        }
    }


}
