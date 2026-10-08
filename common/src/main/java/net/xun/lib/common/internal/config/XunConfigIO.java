package net.xun.lib.common.internal.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.ConfigDirectoryMode;
import net.xun.lib.common.api.config.ConfigEntry;
import net.xun.lib.common.api.config.ConfigGroup;
import net.xun.lib.common.api.config.ConfigType;
import net.xun.lib.common.platform.Services;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;

public final class XunConfigIO {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static ConfigDirectoryMode directoryMode = ConfigDirectoryMode.MOD_DIRECTORY;

    private XunConfigIO() {
    }

    /**
     * Loads values from a JSON file into a config definition.
     * <p>
     * The existing values in the definition act as defaults.
     * <p>
     * Loading is atomic:
     * file -> detached copy -> apply to target
     */
    public static boolean load(ConfigDefinition config, Path path, String modId) {
        if (config == null || !exists(path)) {
            return false;
        }

        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement root = JsonParser.parseReader(reader);

            if (!root.isJsonObject()) {
                throw new IllegalStateException("Root JSON value is not an object.");
            }

            ConfigDefinition loaded = config.createEditorCopy();

            applyJson(loaded, loaded.configClass, loaded.instance, "", root.getAsJsonObject());

            config.applyValuesFrom(loaded);

            return true;
        }
        catch (Exception e) {
            XunLibConstants.LOGGER.error("Error loading config for mod '{}': {}. " + "Existing/default values will be preserved.", modId, path, e);

            return false;
        }
    }

    private static void applyJson(ConfigDefinition definition, Class<?> configClass, Object owner, String parentPath, JsonObject json) {
        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isTransient(modifiers)) {
                continue;
            }

            field.setAccessible(true);

            String path = joinPath(parentPath, field.getName());

            if (field.isAnnotationPresent(ConfigEntry.class)) {
                JsonElement element = json.get(field.getName());

                if (element == null || element.isJsonNull()) {
                    continue;
                }

                Object value = GSON.fromJson(element, field.getGenericType());

                definition.setFieldValue(field, owner, path, value);

                continue;
            }

            ConfigGroup group = field.getAnnotation(ConfigGroup.class);

            if (group == null) {
                continue;
            }

            JsonElement element = json.get(field.getName());
            if (element == null || !element.isJsonObject()) {
                continue;
            }

            Object child = definition.getFieldValue(field, owner, path);
            if (child == null) {
                continue;
            }

            applyJson(definition, field.getType(), child, path, element.getAsJsonObject());
        }
    }

    public static void save(ConfigDefinition config) {
        if (config == null || config.path == null) {
            return;
        }

        try {
            Path path = config.path;

            if (path.getParent() != null) {
                Files.createDirectories(path.getParent());
            }

            JsonObject json = serializeObject(config, config.configClass, config.instance, "");

            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(json, writer);
            }
        }
        catch (Exception e) {
            XunLibConstants.LOGGER.error("Error saving config for mod '{}' ({})", config.modId, config.type, e);
        }
    }

    private static JsonObject serializeObject(ConfigDefinition definition, Class<?> configClass, Object owner, String parentPath) {
        JsonObject object = new JsonObject();

        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isTransient(modifiers)) {
                continue;
            }

            field.setAccessible(true);

            String path = joinPath(parentPath, field.getName());

            if (field.isAnnotationPresent(ConfigEntry.class)) {
                Object value = definition.getFieldValue(field, owner, path);

                object.add(field.getName(), GSON.toJsonTree(value));

                continue;
            }

            ConfigGroup group = field.getAnnotation(ConfigGroup.class);

            if (group == null) {
                continue;
            }

            Object child = definition.getFieldValue(field, owner, path);

            if (child == null) {
                continue;
            }

            object.add(field.getName(), serializeObject(definition, field.getType(), child, path));
        }

        return object;
    }

    public static Object createDefaultConfig(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to create default config instance for " + configClass.getName(), e);
        }
    }

    /**
     * Creates a detached instance copy.
     * <p>
     * Static fields are intentionally ignored here because they are
     * copied by ConfigDefinition into its detached static-value map.
     */
    public static Object copyConfigInstance(Object source, Class<?> configClass) {
        if (source == null) {
            return null;
        }

        try {
            Object target = configClass.getDeclaredConstructor().newInstance();
            copyInstanceFields(source, target, configClass);

            return target;
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to create config copy for " + configClass.getName(), e);
        }
    }

    private static void copyInstanceFields(Object source, Object target, Class<?> configClass) {
        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers)) {
                continue;
            }
            field.setAccessible(true);

            try {
                ConfigGroup group = field.getAnnotation(ConfigGroup.class);
                Object sourceValue = field.get(source);

                if (group != null) {
                    Object targetValue = field.get(target);

                    if (sourceValue == null) {
                        if (!Modifier.isFinal(modifiers)) {
                            field.set(target, null);
                        }

                        continue;
                    }

                    if (targetValue == null) {
                        if (Modifier.isFinal(modifiers)) {
                            throw new IllegalStateException("Final @ConfigGroup field '" + field.getName() + "' was initialized to null.");
                        }
                        field.set(target, copyConfigInstance(sourceValue, field.getType()));
                    }
                    else {
                        copyInstanceFields(sourceValue, targetValue, field.getType());
                    }

                    continue;
                }

                if (field.isAnnotationPresent(ConfigEntry.class)) {
                    Object value = copyConfigValue(sourceValue, field.getGenericType(), false);

                    if (Modifier.isFinal(modifiers)) {
                        throw new IllegalStateException("@ConfigEntry field '" + field.getName() + "' cannot be final.");
                    }

                    field.set(target, value);
                }
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to copy config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'", e);
            }
        }
    }

    /**
     * Copies an individual config value.
     * <p>
     * For @ConfigGroup values, recursively creates an independent object.
     * <p>
     * For regular @ConfigEntry values, Gson performs the value copy.
     */
    public static Object copyConfigValue(Object value, Type type, boolean group) {
        if (value == null) {
            return null;
        }

        if (group) {
            if (!(type instanceof Class<?> groupClass)) {
                throw new IllegalStateException("Config groups must use a concrete class type.");
            }
            return copyConfigInstance(value, groupClass);
        }

        return GSON.fromJson(GSON.toJsonTree(value), type);
    }

    public static Path resolveConfigPath(String modId, String fileName) {
        Path configDir = Services.PLATFORM.getConfigDir();

        if (directoryMode == ConfigDirectoryMode.MOD_DIRECTORY) {
            configDir = configDir.resolve(modId);
        }

        return configDir.resolve(fileName);
    }

    public static Path resolveServerOverridePath(MinecraftServer server, String modId, String fileName) {
        Path configDir = Services.PLATFORM.getServerConfigOverrideDir(server);

        if (directoryMode == ConfigDirectoryMode.MOD_DIRECTORY) {
            configDir = configDir.resolve(modId);
        }

        return configDir.resolve(fileName);
    }

    public static String defaultFileName(String modId, ConfigType type) {
        return switch (type) {
            case CLIENT -> modId + "-client.json";
            case SERVER -> modId + "-server.json";
            case COMMON -> modId + "-common.json";
        };
    }

    public static void setDirectoryMode(ConfigDirectoryMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Directory mode cannot be null.");
        }
        directoryMode = mode;
    }

    public static ConfigDirectoryMode getDirectoryMode() {
        return directoryMode;
    }

    public static boolean exists(Path path) {
        return path != null && Files.exists(path);
    }

    private static String joinPath(String parentPath, String fieldName) {
        return parentPath.isEmpty() ? fieldName : parentPath + "." + fieldName;
    }
}