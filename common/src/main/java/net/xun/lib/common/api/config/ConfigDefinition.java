package net.xun.lib.common.api.config;

import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

public class ConfigDefinition {
    public final String modId;
    public final ConfigType type;

    /**
     * Base/global configuration path (config/).
     */
    public final Path basePath;

    /**
     * Currently active file.
     * <p>
     * For CLIENT/COMMON this is always basePath.
     * For SERVER this becomes the world override when one exists.
     */
    public volatile Path path;

    /**
     * Persistent config instance.
     * <p>
     * Its identity never changes after registration.
     */
    public final Object instance;

    public final Class<?> configClass;

    private volatile MinecraftServer server;

    private final List<ConfigOption> options = new ArrayList<>();
    private final Map<String, ConfigOption> optionsByFieldName = new HashMap<>();

    public ConfigDefinition(String modId, ConfigType type, Path basePath, Object instance, Class<?> configClass) {
        this.modId = modId;
        this.type = type;
        this.basePath = basePath;
        this.path = basePath;
        this.instance = instance;
        this.configClass = configClass;

        parseFields();
    }

    private void parseFields() {
        for (Field field : configClass.getDeclaredFields()) {
            if (!field.isAnnotationPresent(ConfigEntry.class)) {
                continue;
            }

            ConfigOption option = new ConfigOption(this, field, instance);

            options.add(option);
            optionsByFieldName.put(field.getName(), option);
        }
    }

    /**
     * Returns the logical server currently owning this SERVER config.
     */
    public MinecraftServer getServer() {
        return server;
    }

    /**
     * Internal: activates this definition for a logical server.
     */
    void activateServer(MinecraftServer server, Path path) {
        this.server = server;
        this.path = path;
    }

    /**
     * Internal: deactivates this definition when its logical server stops.
     */
    void deactivateServer(MinecraftServer server) {
        if (this.server == server) {
            this.server = null;
            this.path = basePath;
        }
    }

    /**
     * Creates a separate copy for GUI editing.
     */
    public ConfigDefinition createEditorCopy() {
        Object copy = XunConfigManager.copyConfigInstance(instance, configClass);

        return new ConfigDefinition(modId, type, basePath, copy, configClass);
    }

    /**
     * Copies the values from another definition into this definition's
     * persistent instance.
     */
    void applyValuesFrom(ConfigDefinition source) {
        if (!configClass.equals(source.configClass)) {
            throw new IllegalArgumentException("Cannot copy config values between different config classes: " + configClass.getName() + " and " + source.configClass.getName());
        }

        XunConfigManager.copyConfigValues(instance, source.instance, configClass);
    }

    public ConfigOption findOption(String name) {
        return optionsByFieldName.get(name);
    }

    /**
     * @return list of category translation-keys in declaration order.
     */
    public List<String> getCategoryKeys() {
        Set<String> cats = new LinkedHashSet<>();

        for (ConfigOption opt : options) {
            cats.add(opt.categoryKey);
        }

        return new ArrayList<>(cats);
    }

    public List<ConfigOption> getOptionsInCategory(String categoryKey) {
        List<ConfigOption> result = new ArrayList<>();

        for (ConfigOption opt : options) {
            if (opt.categoryKey.equals(categoryKey)) {
                result.add(opt);
            }
        }

        return result;
    }

    /**
     * Human-readable fallback for a category translation key.
     */
    public String getCategoryFallbackName(String categoryKey) {
        for (ConfigOption opt : options) {
            if (opt.categoryKey.equals(categoryKey)) {
                return opt.categoryFallback;
            }
        }

        return categoryKey;
    }
}