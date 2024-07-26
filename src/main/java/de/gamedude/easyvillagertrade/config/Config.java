package de.gamedude.easyvillagertrade.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Map<String, JsonElement> properties;
    private final File file;

    public Config(String fileName) {
        this.properties = new HashMap<>();
        this.file = getOrCreateFile(fileName);

        if(file.length() == 0) {
            loadDefaultProperties();
            savePropertiesToFile();
        }
        this.loadDefaultProperties(); // load defaults
        this.loadPropertiesFromFile(); // override values
        Runtime.getRuntime().addShutdownHook(new Thread(this::savePropertiesToFile));
    }

    private void loadPropertiesFromFile() {
        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);

            for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        } catch ( IOException e ) {
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    public void savePropertiesToFile() {
        JsonObject jsonObject = new JsonObject();

        for(Map.Entry<String, JsonElement> entry : properties.entrySet()) {
            jsonObject.add(entry.getKey(), entry.getValue());
        }

        String json = gson.toJson(jsonObject);
        writeToFile(json);
    }

    public JsonElement getProperty(String key) {
        return this.properties.get(key);
    }

    public void addProperty(String key, Object obj) {
        JsonElement element = gson.toJsonTree(obj);
        this.properties.put(key, element);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getOrCreateFile(String fileName) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "config/" + fileName + ".json");
        file.getParentFile().mkdirs();
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) { throw new RuntimeException(e); }
        }
        return file;
    }

    private void writeToFile(String content) {
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(content);
        } catch (IOException e) {
            LogUtils.getLogger().error("Could not write to file: ");
            LogUtils.getLogger().error(e.getMessage());
        }
    }

    private void loadDefaultProperties() {
        addProperty("preventAxeBreakingValue", 10);
        addProperty("debugEnchantments", false);
    }

}

