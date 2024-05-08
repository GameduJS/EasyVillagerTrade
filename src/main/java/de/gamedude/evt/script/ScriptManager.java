package de.gamedude.evt.script;

import com.mojang.logging.LogUtils;
import de.gamedude.evt.handler.Handler;
import de.gamedude.evt.handler.TradeWorkflow;
import de.gamedude.evt.logic.MalformedParameterException;
import de.gamedude.evt.logic.State;
import de.gamedude.evt.utils.StateType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ScriptManager implements Handler {

    private final Path destinationPath = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt/scripts/");
    private final TradeWorkflow tradeWorkflow;
    private Script script;

    public ScriptManager(TradeWorkflow tradeWorkflow) {
        this.tradeWorkflow = tradeWorkflow;
    }

    public void copyDefaultToCache() {
        if(!destinationPath.toFile().exists())
            destinationPath.toFile().mkdirs();
        if(destinationPath.resolve("defaultscript").toFile().exists())
            return;
        try {
            ResourceManager resourceManager = MinecraftClient.getInstance().getResourceManager();
            Map<Identifier, Resource> resourcesInDirectory = resourceManager.findResources("defaultscript", path -> true);
            for (Identifier resourceLocation : resourcesInDirectory.keySet()) {
                Resource resource = resourcesInDirectory.get(resourceLocation);
                String fileName = resourceLocation.getPath();

                Path destinationFilePath = destinationPath.resolve(fileName);
                Files.createDirectories(destinationFilePath.getParent());

                try (InputStream inputStream = resource.getInputStream();
                     OutputStream outputStream = Files.newOutputStream(destinationFilePath)) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<State> parseScriptContent(File file) {
        List<State> states = new ArrayList<>();
        List<String> lines;
        try {
            lines = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String line : lines) {
            String[] objects = line.split(" ");
            String[] parameters = (Arrays.copyOfRange(objects, 1, objects.length));

            try {
                State state = StateType.getByToken(objects[0]).getState(tradeWorkflow, parameters);
                states.add(state);
            } catch ( MalformedParameterException e ) {
                LogUtils.getLogger().error("Unable to run script!");
                LogUtils.getLogger().error(e.getMessage() + " at line " + lines.indexOf(line));
                return states;
            }
        }
        return states;
    }

    /**
     * TODO: load default script to config cache
     */
    public void loadScript(String scriptDirectory) throws Exception {
        File[] files = destinationPath.resolve("/" + scriptDirectory).toFile().listFiles((dir, name) -> name.endsWith(".txt"));
        if(files == null)
            throw new Exception("Directory is empty or does not have any scripts");
        Map<Script.ScriptType, List<State>> typeScriptMap = new HashMap<>();
        for (File file : files) {
            Script.ScriptType type = Script.ScriptType.byFileName(file.getName());
            List<State> states = parseScriptContent(file);
            typeScriptMap.put(type, states);
        }

        if(typeScriptMap.size() != 3)
            throw new Exception("Incomplete script. Please provide the scripts of type 'init', 'repeat', 'found'");

        this.script = new Script(typeScriptMap);
    }

    public Script getScript() {
        return script;
    }

    public List<String> getStoredScriptNames() {
        return Arrays.stream(Optional.ofNullable(destinationPath.toFile().listFiles((dir, name) -> dir.isDirectory())).orElse(new File[0])).map(File::getName).toList();
    }




}
