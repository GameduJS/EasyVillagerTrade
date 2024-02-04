package de.gamedude.easyvillagertrade.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec2f;

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

        if(file.length() == 0)
            writeToFile("{}");
        this.loadDefaultProperties();
        this.loadPropertiesFromFile();

        Runtime.getRuntime().addShutdownHook(new Thread(this::savePropertiesToFile));
    }

    private void loadPropertiesFromFile() {
        try (FileReader fileReader = new FileReader(file)) {
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);

            for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                properties.put(entry.getKey(), entry.getValue());
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private void savePropertiesToFile() {
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

    public void removeProperty(String key) {
        properties.remove(key);
    }

    private File getOrCreateFile(String fileName) {
        File file = new File(MinecraftClient.getInstance().runDirectory, "/config/evt/" + fileName + ".json");
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
            e.printStackTrace();
        }
    }

    private void loadDefaultProperties() {
        Map<String, Object> defaultProps = new HashMap<>();
        defaultProps.put("distanceX", 2);
        defaultProps.put("distanceZ", 2);
        defaultProps.put("soundPlayed", SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK.getId().toString());
        defaultProps.put("shouldPlaySound", true);

        defaultProps.forEach(this::addProperty);
    }

}
