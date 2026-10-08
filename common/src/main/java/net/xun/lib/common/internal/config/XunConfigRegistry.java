package net.xun.lib.common.internal.config;

import net.minecraft.server.MinecraftServer;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.config.ConfigDefinition;
import net.xun.lib.common.api.config.ConfigKey;
import net.xun.lib.common.api.config.ConfigType;
import net.xun.lib.common.api.config.XunConfig;
import net.xun.lib.common.platform.Services;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public final class XunConfigRegistry {

    private static final Map<ConfigKey, ConfigDefinition> CONFIGS = new HashMap<>();

    /**
     * Global/base snapshots.
     * <p>
     * These are always detached definitions.
     * <p>
     * They represent the global config without any world override.
     */
    private static final Map<ConfigKey, ConfigDefinition> BASE_CONFIGS = new HashMap<>();

    private static volatile MinecraftServer currentServer;

    private XunConfigRegistry() {
    }

    public static synchronized <T> T register(Class<T> configClass) {
        XunConfig annotation = configClass.getAnnotation(XunConfig.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Class " + configClass.getName() + " is missing @XunConfig annotation");
        }

        String modId = annotation.modId();
        ConfigType type = annotation.type();

        String fileName = annotation.fileName().isEmpty() ? XunConfigIO.defaultFileName(modId, type) : annotation.fileName();

        ConfigKey key = ConfigKey.of(modId, type);

        if (CONFIGS.containsKey(key)) {
            throw new IllegalStateException("Config already registered for mod '" + modId + "' with type '" + type + "'");
        }

        Path basePath = XunConfigIO.resolveConfigPath(modId, fileName);
        T instance = createDefaultConfig(configClass);

        ConfigDefinition definition = new ConfigDefinition(modId, type, basePath, instance, configClass);

        /*
         * Load the global configuration into the live values.
         *
         * The definition already contains Java defaults.
         */
        XunConfigIO.load(definition, basePath, modId);
        CONFIGS.put(key, definition);

        /*
         * Store the global state independently.
         *
         * This becomes the source from which SERVER world
         * configurations are layered.
         */
        BASE_CONFIGS.put(key, definition.createEditorCopy());

        save(definition);
        return instance;
    }

    private static <T> T createDefaultConfig(Class<T> configClass) {
        try {
            var constructor = configClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        }
        catch (Exception e) {
            throw new IllegalStateException("Unable to create default config instance for " + configClass.getName(), e);
        }
    }

    /*
     * -------------------------------------------------------------------------
     * SERVER LIFECYCLE
     * -------------------------------------------------------------------------
     */

    public static synchronized void loadServerConfigs(MinecraftServer server) {
        if (server == null) {
            return;
        }

        if (currentServer != null && currentServer != server) {
            unloadServerConfigs(currentServer);
        }

        currentServer = server;

        for (Map.Entry<ConfigKey, ConfigDefinition> entry : CONFIGS.entrySet()) {
            ConfigDefinition config = entry.getValue();

            if (config.type != ConfigType.SERVER) {
                continue;
            }

            loadServerConfig(entry.getKey(), config, server);
        }
    }

    private static void loadServerConfig(ConfigKey key, ConfigDefinition config, MinecraftServer server) {
        ConfigDefinition base = BASE_CONFIGS.get(key);
        ConfigDefinition effective = base != null ? base.createEditorCopy() : config.createEditorCopy();

        String fileName = config.basePath.getFileName().toString();

        Path overridePath = XunConfigIO.resolveServerOverridePath(server, config.modId, fileName);
        Path activePath = config.basePath;

        if (XunConfigIO.exists(overridePath)) {
            XunConfigIO.load(effective, overridePath, config.modId);
            activePath = overridePath;
        }

        /*
         * Apply the effective server state to the actual live config.
         *
         * For static config fields this updates the real static
         * values. The GUI/editor definitions remain detached.
         */
        config.applyValuesFrom(effective);
        config.activateServer(server, activePath);
    }

    public static synchronized void unloadServerConfigs(MinecraftServer server) {
        if (server == null || currentServer != server) {

            return;
        }

        /*
         * Save the effective SERVER configuration first.
         */
        for (Map.Entry<ConfigKey, ConfigDefinition> entry : CONFIGS.entrySet()) {
            ConfigDefinition config = entry.getValue();

            if (entry.getKey().type() == ConfigType.SERVER) {
                save(config);
            }
        }

        /*
         * Restore the global/base state.
         *
         * This is essential with static fields.
         */
        for (Map.Entry<ConfigKey, ConfigDefinition> entry : CONFIGS.entrySet()) {
            ConfigKey key = entry.getKey();

            if (key.type() != ConfigType.SERVER) {
                continue;
            }

            ConfigDefinition config = entry.getValue();

            ConfigDefinition base = BASE_CONFIGS.get(key);

            if (base != null) {
                config.applyValuesFrom(base);
            }

            config.deactivateServer(server);
        }

        currentServer = null;
    }

    public static MinecraftServer getCurrentServer() {
        return currentServer;
    }

    public static boolean hasLogicalServer() {
        return currentServer != null;
    }

    public static ConfigDefinition get(String modId, ConfigType type) {
        return CONFIGS.get(ConfigKey.of(modId, type));
    }

    public static Map<ConfigType, ConfigDefinition> getAll(String modId) {
        Map<ConfigType, ConfigDefinition> result = new EnumMap<>(ConfigType.class);

        CONFIGS.forEach((key, config) -> {
            if (key.modId().equals(modId)) {
                result.put(key.type(), config);
            }
        });

        return Map.copyOf(result);
    }

    public static void save(String modId, ConfigType type) {
        ConfigDefinition config = get(modId, type);

        if (config != null) {
            save(config);
        }
    }

    public static void save(ConfigDefinition config) {
        if (config == null) {
            return;
        }

        if (!canEdit(config)) {
            XunLibConstants.LOGGER.warn("Refusing to save {} config for mod '{}': " + "the current execution context is not authoritative.", config.type, config.modId);
            return;
        }

        if (config.type == ConfigType.SERVER && currentServer != null) {
            MinecraftServer server = currentServer;

            if (!server.isSameThread()) {
                server.execute(() -> saveNow(config));
            }
            else {
                saveNow(config);
            }

            return;
        }
        saveNow(config);
    }

    private static void saveNow(ConfigDefinition config) {
        XunConfigIO.save(config);

        if (config.type == ConfigType.SERVER && config.path != null && config.path.equals(config.basePath)) {

            refreshBaseSnapshot(config);
        }
    }

    private static void refreshBaseSnapshot(ConfigDefinition config) {
        ConfigKey key = ConfigKey.of(config.modId, config.type);

        BASE_CONFIGS.put(key, config.createEditorCopy());
    }

    public static void saveEdited(ConfigDefinition target, ConfigDefinition edited) {
        if (target == null || edited == null) {
            return;
        }

        if (!target.modId.equals(edited.modId) || target.type != edited.type || target.configClass != edited.configClass) {

            throw new IllegalArgumentException("Edited config does not match target config.");
        }

        if (!canEdit(target)) {
            XunLibConstants.LOGGER.warn("Refusing to apply {} config for mod '{}': " + "the current execution context is not authoritative.", target.type, target.modId);

            return;
        }

        Runnable task = () -> {
            target.applyValuesFrom(edited);
            saveNow(target);
        };

        if (target.type == ConfigType.SERVER && currentServer != null) {
            MinecraftServer server = currentServer;

            if (server.isSameThread()) {
                task.run();
            }
            else {
                server.execute(task);
            }
            return;
        }

        task.run();
    }

    public static void saveAll(String modId) {
        CONFIGS.forEach((key, config) -> {
            if (key.modId().equals(modId)) {
                save(key.modId(), key.type());
            }
        });
    }

    public static boolean canEdit(ConfigDefinition config) {
        return switch (config.type) {
            case CLIENT -> Services.PLATFORM.isPhysicalClient();
            case COMMON -> true;
            case SERVER -> currentServer == null || config.getServer() == currentServer;
        };
    }
}