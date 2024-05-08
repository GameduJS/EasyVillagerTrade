package de.gamedude.evt.script;

import com.mojang.logging.LogUtils;
import de.gamedude.evt.logic.MalformedParameterException;
import de.gamedude.evt.logic.State;
import de.gamedude.evt.utils.StateType;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Script {

    private final Path destinationPath = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt/scripts/");

    private  List<State> initProcess;
    private  List<State> mainProcess;
    private  List<State> tradeFoundProcess;

    private Map<ScriptType, List<State>> typeScriptMap;

    private Iterator<State> currentIterator;
    private State currentState = null;


    public Script(Map<ScriptType, List<State>> typeScriptMap) {
        this.typeScriptMap = typeScriptMap;
    }

    public void tickInitial() {
        this.currentIterator = initProcess.iterator();
    }

    public void tickRepetition() {
        this.currentIterator = mainProcess.iterator();
    }

    public void tickTradeFound() {
        this.currentIterator = tradeFoundProcess.iterator();
    }

    public void tick() {
        if(currentIterator == null || !currentIterator.hasNext())
            return;
        if(currentState == null)
            currentState = currentIterator.next();
        // currentState#run == true | completed
        if(currentState.run() == 1)
            currentState = currentIterator.next();
    }



    public enum ScriptType {
        INIT,
        REPEAT,
        FOUND;

        private final static ScriptType[] VALUES = values();
        public static ScriptType byFileName(String name) throws Exception {
            for (ScriptType value : VALUES) {
                if(name.toLowerCase().contains(value.name().toLowerCase()))
                    return value;
            }
            throw new Exception("'" + name + "' is not a suitable name for a type of script.");
        }
    }


}
