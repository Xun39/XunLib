package net.xun.lib.common.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.xun.lib.common.platform.Services;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class XunLibConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, ConfigDefinition> CONFIGS_BY_MOD_ID = new HashMap<>();

    public static <T> T registerConfig(Class<T> configClass) {
        XunConfig annotation = configClass.getAnnotation(XunConfig.class);
        if (annotation == null) {
            throw new IllegalArgumentException("Class " + configClass.getName() + " is missing @XLConfig annotation");
        }

        String modId = annotation.modId();
        String fileName = annotation.fileName().isEmpty() ? modId + ".json" : annotation.fileName();
        Path path = Services.PLATFORM.getConfigDir().resolve(fileName);

        T instance;
        try {
            if (Files.exists(path)) {
                try (Reader reader = Files.newBufferedReader(path)) {
                    instance = GSON.fromJson(reader, configClass);
                    if (instance == null) {
                        instance = configClass.getDeclaredConstructor().newInstance();
                    }
                }
            }
            else {
                instance = configClass.getDeclaredConstructor().newInstance();
            }
        }
        catch (Exception e) {
            System.err.println("[XunLib] Error loading config for " + modId + ", falling back to defaults.");
            try {
                instance = configClass.getDeclaredConstructor().newInstance();
            }
            catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        ConfigDefinition config = new ConfigDefinition(modId, path, instance, configClass);
        CONFIGS_BY_MOD_ID.put(modId, config);
        saveConfig(modId);

        return instance;
    }

    public static void saveConfig(String modId) {
        ConfigDefinition config = CONFIGS_BY_MOD_ID.get(modId);
        if (config == null) return;
        try {
            if (config.path.getParent() != null) {
                Files.createDirectories(config.path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(config.path)) {
                GSON.toJson(config.instance, writer);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ConfigDefinition getConfig(String modId) {
        return CONFIGS_BY_MOD_ID.get(modId);
    }
}