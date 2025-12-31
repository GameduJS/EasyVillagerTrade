package de.gamedude.evt.script;

import de.gamedude.evt.automation.State;
import de.gamedude.evt.automation.StateFactory;
import de.gamedude.evt.automation.StateRegistry;
import de.gamedude.evt.automation.states.*;
import de.gamedude.evt.handler.Handler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScriptManager implements Handler {

    static {
        StateRegistry.registerState("BREAK", BreakState::parse);
        StateRegistry.registerState("BUY", BuyState::parse);
        StateRegistry.registerState("CHECK", CheckState::parse);
        StateRegistry.registerState("INTERACT", InteractState::parse);
        StateRegistry.registerState("LOOK", LookState::parse);
        StateRegistry.registerState("WALK", MoveState::parse);
        StateRegistry.registerState("PLACE", PlaceState::parse);
        StateRegistry.registerState("SELECT", SelectState::parse);
    }

    public static final Logger LOGGER = Logger.getLogger("Script");
    private final Path destinationPath = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt/scripts/");

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
            LOGGER.log(Level.SEVERE, "Failed to copy default script", e);
        }
    }

    public List<State> parseScript(List<String> lines) {
        List<State> actions = new ArrayList<>();

        for( int i = 0; i < lines.size(); i++ ) {
            String line = lines.get(i).trim();
            // Comment or blank line
            if(line.startsWith("#") || line.isBlank()) continue;

            String[] parts = line.split("\\s+");
            String command = parts[0];
            String[] args = Arrays.copyOfRange(parts, 1, parts.length);

            StateFactory factory = StateRegistry.get(command);

            if ( factory == null ) {
                // command does not exist
                throw new RuntimeException("Unknown command: " + command);
            }

            try {
                ParsingContext ctx = new ParsingContext(lines, i);

                State state = factory.create(args, ctx);
                actions.add(state);
            } catch ( Exception e ) {
                throw new RuntimeException("Fehler in Zeile " + (i + 1) + " (" + command + "): " + e.getMessage());
            }
        }

       return actions;
    }
}
