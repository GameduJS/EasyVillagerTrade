package de.gamedude.easyvillagertrade.scripting.core;

import de.gamedude.easyvillagertrade.scripting.core.script.Script;
import de.gamedude.easyvillagertrade.scripting.core.script.actions.*;
import de.gamedude.easyvillagertrade.scripting.core.script.actions.base.Action;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.Matrix4f;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;

/**
 *
 */
public class ScriptProcessor {

    private static final Path SCRIPT_PATH = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt");

    public ScriptProcessor() {
        this.setupPath();
    }

    private void setupPath() {
        if (!Files.exists(SCRIPT_PATH)) {
            try {
                Files.createDirectories(SCRIPT_PATH);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Loads every text file in {@code SCRIPT_PATH} directory as a script
     */
    public Map<String, Script> loadScripts() {
        Map<String, Script> scriptMap = new HashMap<>();
        File[] files = SCRIPT_PATH.toFile().listFiles(pathname -> pathname.getName().endsWith(".txt"));
        if(files == null) return scriptMap;

        for (File scriptFile : files) {
            String scriptName = scriptFile.getName().replace(".txt", "");
            List<String> fileContent = readFile(scriptFile.getPath());
            Script script = generateScript(fileContent);

            scriptMap.put(scriptName, script);
        }
        return scriptMap;
    }

    /**
     * <p>The method that the raw script and turns its single commands
     * into a {@link List<Runnable> } of actions.
     *
     * @param rawScript String version from file
     * @return {@link Script}
     */
    private Script generateScript(List<String> rawScript) {
        List<Action> actionList = new ArrayList<>();
        Iterator<String> iterator = rawScript.iterator();
        ConditionalAction conditialAction = null;

        while (iterator.hasNext()) {
            String line = iterator.next();

            /* Currently not available
            if(line.startsWith("IF")) {

                conditialAction = new ConditionalAction(line.split(" ")[1]);
                do {
                    Action ifAction = processCommand(line.trim());
                    conditialAction.addConditionalAction(true, ifAction);
                } while(iterator.hasNext() && (line = iterator.next()).startsWith("ELSE"));

            }
            if(line.startsWith("ELSE")) {
                do {
                    Action elseAction = processCommand(line.trim());
                    conditialAction.addConditionalAction(false, elseAction);
                } while(iterator.hasNext() && (line = iterator.next()).startsWith(" "));
                actionList.add(conditialAction);

            } else {

            } */

            actionList.add(processCommand(line));
        }

        return new Script(actionList);
    }

    private Action processCommand(String line) {
        Action action = null;
        String[] arguments = Arrays.copyOfRange(line.trim().split(" "), 1, line.split(" ").length);

        if(line.startsWith("WALK")) {
            String direction = arguments[0];
            String blocks = arguments[1];
           action = new WalkAction(direction, blocks);
        } else if(line.startsWith("EXECUTE")) {
            action = new ExecuteAction();
        } else if(line.startsWith("SETTURN")) {
            String yaw = arguments[0];
            String pitch = arguments[1];
            action = new TurnAction(yaw, pitch);
        } else if(line.startsWith("SELECT")) {
            action = new SelectAction();
        }

        return action;
    }

    private List<String> readFile(String path) {
        List<String> lines = List.of();
        try {
            lines = Files.readAllLines(Path.of(path));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return lines;
    }
}
