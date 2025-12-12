package de.gamedude.evt.script;

import com.mojang.logging.LogUtils;
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

public class ScriptManager implements Handler {

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
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    private void parseScript(List<String> rawContent) {
        for(String line : rawContent) {
            // Comment or blank line
            if(line.startsWith("#") || line.isBlank()) continue;

            String[] parts = line.split(" ");
            String command = parts[0];                                                                                                                              // EXAMPLE:
            String[] arguments = Arrays.copyOfRange(parts, 1, parts.length, String[].class);      // COMMAND ARG_1 ARG_2 ... ARG_N

            // Create state based on command
            // parse arguments from string into object

        }
    }

}
