package net.xun.lib.common.api.config;

import com.google.gson.*;
import net.minecraft.server.MinecraftServer;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.platform.Services;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class XunConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<ConfigKey, ConfigDefinition> CONFIGS = new HashMap<>();

    private static ConfigDirectoryMode directoryMode;

    /**
     * The logical server owned by this JVM, if one is currently running.
     * <p>
     * On an integrated server this is the IntegratedServer instance.
     * On a dedicated server this is the DedicatedServer instance.
     */
    private static volatile MinecraftServer currentServer;

    private XunConfigManager() {
    }

    public static <T> T registerConfig(Class<T> configClass) {
        XunConfig annotation = configClass.getAnnotation(XunConfig.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Class " + configClass.getName() + " is missing @XunConfig annotation");
        }

        String modId = annotation.modId();
        ConfigType type = annotation.type();

        String fileName = annotation.fileName().isEmpty() ? defaultFileName(modId, type) : annotation.fileName();

        ConfigKey key = ConfigKey.of(modId, type);

        if (CONFIGS.containsKey(key)) {
            throw new IllegalStateException("Config already registered for mod '" + modId + "' with type '" + type + "'");
        }

        Path basePath = resolveConfigPath(modId, fileName);

        @SuppressWarnings("unchecked")
        T instance = (T) loadConfig(configClass, basePath, modId, createDefaultConfig(configClass));

        ConfigDefinition definition = new ConfigDefinition(modId, type, basePath, instance, configClass);
        CONFIGS.put(key, definition);

        saveConfig(definition);

        return instance;
    }

    /**
     * Called by the loader when a logical server starts.
     * <p>
     * This works for both IntegratedServer and DedicatedServer.
     */
    public static synchronized void loadServerConfigs(MinecraftServer server) {
        if (server == null) {
            return;
        }

        if (currentServer != null && currentServer != server) {
            unloadServerConfigs(currentServer);
        }

        currentServer = server;

        for (ConfigDefinition definition : CONFIGS.values()) {
            if (definition.type != ConfigType.SERVER) {
                continue;
            }

            loadServerConfig(definition, server);
        }
    }

    private static void loadServerConfig(ConfigDefinition config, MinecraftServer server) {
        Object defaults = createDefaultConfig(config.configClass);

        // Layer the global config over the defaults.
        Object base = loadConfig(config.configClass, config.basePath, config.modId, defaults);

        String fileName = config.basePath.getFileName().toString();
        Path overridePath = resolveServerOverridePath(server, config.modId, fileName);

        Object effective = base;
        Path activePath = config.basePath;

        // If a world override exists, layer it over the global config.
        if (Files.exists(overridePath)) {
            effective = loadConfig(config.configClass, overridePath, config.modId, base);
            activePath = overridePath;
        }

        // Preserve the identity of the registered config object.
        copyConfigValues(config.instance, effective, config.configClass);
        config.activateServer(server, activePath);
    }

    /**
     * Called by the loader when a logical server stops.
     */
    public static synchronized void unloadServerConfigs(MinecraftServer server) {
        if (server == null || currentServer != server) {
            return;
        }

        // Save while the logical server is still authoritative.
        CONFIGS.forEach((key, definition) -> {
            if (key.type() == ConfigType.SERVER) {
                saveConfig(definition);
            }
        });

        CONFIGS.forEach((key, definition) -> {
            if (key.type() == ConfigType.SERVER) {
                definition.deactivateServer(server);
            }
        });

        currentServer = null;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean hasLogicalServer() {
        return currentServer != null;
    }

    private static Path resolveConfigPath(String modId, String fileName) {
        Path configDir = Services.PLATFORM.getConfigDir();

        if (directoryMode == ConfigDirectoryMode.MOD_DIRECTORY) {
            configDir = configDir.resolve(modId);
        }

        return configDir.resolve(fileName);
    }

    private static Path resolveServerOverridePath(MinecraftServer server, String modId, String fileName) {
        Path configDir = Services.PLATFORM.getServerConfigOverrideDir(server);

        if (directoryMode == ConfigDirectoryMode.MOD_DIRECTORY) {
            configDir = configDir.resolve(modId);
        }

        return configDir.resolve(fileName);
    }

    private static String defaultFileName(String modId, ConfigType type) {
        return switch (type) {
            case CLIENT -> modId + "-client.json";
            case SERVER -> modId + "-server.json";
            case COMMON -> modId + "-common.json";
        };
    }

    public static void setDirectoryMode(ConfigDirectoryMode value) {
        directoryMode = value;
    }

    public static ConfigDirectoryMode getDirectoryMode() {
        return directoryMode;
    }

    private static Object loadConfig(Class<?> configClass, Path path, String modId, Object defaults) {
        if (!Files.exists(path)) {
            return defaults;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement element = JsonParser.parseReader(reader);

            if (!element.isJsonObject()) {
                throw new IllegalStateException("Root JSON value is not an object.");
            }

            JsonObject merged = GSON.toJsonTree(defaults).getAsJsonObject();
            mergeJsonObjects(merged, element.getAsJsonObject());
            Object instance = GSON.fromJson(merged, configClass);

            return instance != null ? instance : defaults;
        }
        catch (Exception e) {
            XunLibConstants.LOGGER.error("Error loading config for mod '{}': {}. Falling back to defaults.", modId, path, e);
            return defaults;
        }
    }

    /**
     * Recursively merges source into target.
     * <p>
     * Objects are merged recursively; all other JSON values replace
     * the target value.
     */
    private static void mergeJsonObjects(JsonObject target, JsonObject source) {
        for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
            String key = entry.getKey();
            JsonElement sourceValue = entry.getValue();

            if (sourceValue.isJsonObject() && target.has(key) && target.get(key).isJsonObject()) {
                mergeJsonObjects(target.getAsJsonObject(key), sourceValue.getAsJsonObject());
            }
            else {
                target.add(key, sourceValue.deepCopy());
            }
        }
    }

    private static Object createDefaultConfig(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to create default config instance for " + configClass.getName(), e);
        }
    }

    public static void saveConfig(String modId, ConfigType type) {
        ConfigDefinition config = getConfig(modId, type);

        if (config == null) {
            return;
        }

        saveConfig(config);
    }

    public static void saveConfig(ConfigDefinition config) {
        if (config == null) {
            return;
        }

        if (!canEdit(config)) {
            XunLibConstants.LOGGER.warn("Refusing to save {} config for mod '{}': " + "the current execution context is not authoritative.", config.type, config.modId);
            return;
        }

        // SERVER file writes should happen on the logical server thread.
        if (config.type == ConfigType.SERVER && currentServer != null) {
            MinecraftServer server = currentServer;

            if (!server.isSameThread()) {
                server.execute(() -> saveConfigNow(config));
            }
            else {
                saveConfigNow(config);
            }

            return;
        }

        saveConfigNow(config);
    }

    private static void saveConfigNow(ConfigDefinition config) {
        Path path = config.path;
        if (path == null) return;

        try {
            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(config.instance, config.configClass, writer);
            }
        }
        catch (Exception e) {
            XunLibConstants.LOGGER.error("Error saving config for mod '{}' ({})", config.modId, config.type, e);
        }
    }

    /**
     * Saves values edited in a GUI/editor definition.
     * <p>
     * SERVER configs are applied on the logical server thread.
     */
    public static void saveEditedConfig(ConfigDefinition target, ConfigDefinition edited) {
        if (target == null || edited == null) {
            return;
        }

        if (!target.modId.equals(edited.modId) || target.type != edited.type || target.configClass != edited.configClass) {
            throw new IllegalArgumentException("Edited config does not match target config.");
        }

        if (!canEdit(target)) {
            XunLibConstants.LOGGER.warn("Refusing to apply {} config for mod '{}': " + "the current environment is not authoritative.", target.type, target.modId);
            return;
        }

        if (target.type == ConfigType.SERVER && currentServer != null) {
            MinecraftServer server = currentServer;

            Runnable task = () -> {
                target.applyValuesFrom(edited);
                saveConfigNow(target);
            };

            if (server.isSameThread()) {
                task.run();
            }
            else {
                server.execute(task);
            }

            return;
        }

        target.applyValuesFrom(edited);
        saveConfigNow(target);
    }

    public static void saveConfigs(String modId) {
        CONFIGS.forEach((key, definition) -> {
            if (key.modId().equals(modId)) {
                saveConfig(key.modId(), key.type());
            }
        });
    }

    /**
     * Determines whether the current execution context is allowed to
     * modify the given configuration.
     * <p>
     * Note that SERVER does NOT mean dedicated server.
     * It means a logical server is currently active.
     */
    public static boolean canEdit(ConfigDefinition config) {
        return switch (config.type) {
            case CLIENT -> Services.PLATFORM.isPhysicalClient();
            case COMMON -> true;
            case SERVER -> currentServer == null || config.getServer() == currentServer;
        };
    }

    public static ConfigDefinition getConfig(String modId, ConfigType type) {
        return CONFIGS.get(ConfigKey.of(modId, type));
    }

    public static Map<ConfigType, ConfigDefinition> getConfigs(String modId) {
        Map<ConfigType, ConfigDefinition> result = new EnumMap<>(ConfigType.class);

        CONFIGS.forEach((key, definition) -> {
            if (key.modId().equals(modId)) {
                result.put(key.type(), definition);
            }
        });

        return Map.copyOf(result);
    }

    /**
     * Deep-copy a config instance through Gson.
     * <p>
     * Used by the configuration screen so it edits a draft rather
     * than the live SERVER config object.
     */
    static Object copyConfigInstance(Object instance, Class<?> configClass) {
        return GSON.fromJson(GSON.toJson(instance), configClass);
    }

    /**
     * Copy fields from one config object into another while preserving
     * the identity of the destination object.
     */
    static void copyConfigValues(Object target, Object source, Class<?> configClass) {
        if (target == null || source == null) {
            return;
        }

        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers)) {
                continue;
            }

            field.setAccessible(true);

            try {
                field.set(target, field.get(source));
            }
            catch (IllegalAccessException e) {
                XunLibConstants.LOGGER.error("Failed to copy config field '{}.{}'", field.getDeclaringClass().getName(), field.getName(), e);
            }
        }
    }
}