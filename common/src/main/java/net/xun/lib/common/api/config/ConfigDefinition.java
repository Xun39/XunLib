package net.xun.lib.common.api.config;

import net.minecraft.server.MinecraftServer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

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
    private final Map<String, ConfigOption> optionsByPath = new HashMap<>();

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
        parseFields(configClass, instance, "", "");
    }

    /**
     * Recursively discovers config entries and flattens them into ConfigOption objects.
     *
     * @param configClass       class currently being inspected
     * @param instance          instance containing the fields
     * @param parentPath        canonical path of the parent group
     * @param inheritedCategory category inherited from the parent group
     */
    private void parseFields(Class<?> configClass, Object instance, String parentPath, String inheritedCategory) {
        for (Field field : configClass.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            field.setAccessible(true);

            if (field.isAnnotationPresent(ConfigEntry.class)) {
                String fieldPath = joinPath(parentPath, field.getName());

                ConfigOption option = new ConfigOption(this, field, instance, fieldPath, inheritedCategory);

                options.add(option);
                optionsByPath.put(fieldPath, option);

                continue;
            }

            ConfigGroup group = field.getAnnotation(ConfigGroup.class);

            if (group == null) {
                continue;
            }

            Object childInstance;

            try {
                childInstance = field.get(instance);
            }
            catch (IllegalAccessException e) {
                throw new IllegalStateException("Failed to access config group '" + field.getName() + "' in " + configClass.getName(), e);
            }

            if (childInstance == null) {
                throw new IllegalStateException("Config group '" + field.getName() + "' in " + configClass.getName() + " is null");
            }

            String groupPath = joinPath(parentPath, field.getName());
            String groupCategory = inheritedCategory;

            if (!group.category().isEmpty()) {
                groupCategory = group.category();
            }

            parseFields(field.getType(), childInstance, groupPath, groupCategory);
        }
    }

    private static String joinPath(String parentPath, String fieldName) {
        return parentPath.isEmpty() ? fieldName : parentPath + "." + fieldName;
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

    /**
     * Finds an option by its canonical path.
     *
     * <p>Examples:</p>
     * <pre>
     * enableArmor
     * armorEffect.froststeel.enableArmor
     * </pre>
     */
    public ConfigOption findOption(String path) {
        return optionsByPath.get(path);
    }

    public ConfigOption findOption(String field, String relativePath) {
        if (field == null || field.isEmpty()) {
            return null;
        }

        ConfigOption exact = optionsByPath.get(field);

        if (exact != null) {
            return exact;
        }

        /*
         * Then resolve relative to the current group.
         */
        if (relativePath != null && !relativePath.isEmpty()) {
            String candidate = relativePath + "." + field;

            ConfigOption relative = optionsByPath.get(candidate);

            if (relative != null) {
                return relative;
            }
        }
        return optionsByPath.get(field);
    }

    /**
     * @return list of category translation-keys in declaration order.
     */
    public List<String> getCategoryKeys() {
        Set<String> categories = new LinkedHashSet<>();

        for (ConfigOption option : options) {
            categories.add(option.categoryKey);
        }

        return new ArrayList<>(categories);
    }

    public List<ConfigOption> getOptionsInCategory(String categoryKey) {
        List<ConfigOption> result = new ArrayList<>();

        for (ConfigOption option : options) {
            if (option.categoryKey.equals(categoryKey)) {
                result.add(option);
            }
        }

        return result;
    }

    /**
     * Human-readable fallback for a category translation key.
     */
    public String getCategoryFallbackName(String categoryKey) {
        for (ConfigOption option : options) {
            if (option.categoryKey.equals(categoryKey)) {
                return option.categoryFallback;
            }
        }

        return categoryKey;
    }
}