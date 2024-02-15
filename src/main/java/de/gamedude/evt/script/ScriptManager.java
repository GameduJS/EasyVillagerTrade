package de.gamedude.evt.script;

import net.minecraft.client.MinecraftClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ScriptManager {

    private final Path destinationPath = Path.of(MinecraftClient.getInstance().runDirectory.getPath(), "/config/evt/scripts/");

    private String loadScriptContent(Path filePath) {
        try {
            return  String.join("\n", Files.readAllLines(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
