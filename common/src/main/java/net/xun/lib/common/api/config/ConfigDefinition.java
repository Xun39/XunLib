package net.xun.lib.common.api.config;

import net.minecraft.server.MinecraftServer;
import net.xun.lib.common.api.util.TranslationUtil;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ConfigDefinition {
    public final String modId;
    public final ConfigType type;

    /**
     * Global/base configuration path.
     */
    public final Path basePath;

    /**
     * Currently active configuration path.
     */
    public volatile Path path;

    /**
     * Root configuration object.
     */
    public final Object instance;

    public final Class<?> configClass;

    private volatile MinecraftServer server;

    /**
     * True when this definition is detached from the live configuration.
     */
    private final boolean detached;

    /**
     * Storage for static fields belonging to detached definitions.
     */
    private final Map<String, Object> staticValues = new HashMap<>();

    /**
     * Flattened configuration options.
     */
    private final List<ConfigOption> options = new ArrayList<>();

    private final Map<String, ConfigOption> optionsByPath = new HashMap<>();

    /**
     * Categories indexed by canonical category path.
     * <p>
     * Example:
     * <p>
     * tool_effect
     * tool_effect.luminium
     * tool_effect.froststeel
     */
    private final Map<String, CategoryNode> categories = new LinkedHashMap<>();

    /**
     * Translation/display key -> canonical category path.
     */
    private final Map<String, String> categoryPathByKey = new HashMap<>();

    public ConfigDefinition(String modId, ConfigType type, Path basePath, Object instance, Class<?> configClass) {
        this(modId, type, basePath, instance, configClass, false);
    }

    public ConfigDefinition(String modId, ConfigType type, Path basePath, Object instance, Class<?> configClass, boolean detached) {
        this.modId = modId;
        this.type = type;
        this.basePath = basePath;
        this.path = basePath;
        this.instance = instance;
        this.configClass = configClass;
        this.detached = detached;

        parseFields();
    }

    public boolean isDetached() {
        return detached;
    }

    private void parseFields() {
        parseFields(configClass, instance, "", "");
    }

    private void parseFields(Class<?> currentClass, Object instance, String fieldPath, String parentCategory) {
        for (Field field : currentClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (Modifier.isTransient(modifiers) || field.isSynthetic()) {
                continue;
            }
            field.setAccessible(true);

            String currentFieldPath = joinPath(fieldPath, field.getName());

            ConfigGroup group = field.getAnnotation(ConfigGroup.class);
            if (group != null) {
                Object child = getFieldValueForParsing(field, instance);

                if (child == null) {
                    throw new IllegalStateException("Config group '" + currentFieldPath + "' is null");
                }

                String segment = !group.category().isEmpty() ? group.category() : humanize(field.getName());

                String categoryPath = parentCategory.isEmpty() ? segment : parentCategory + "." + segment;
                ensureCategoryPath(categoryPath);

                parseFields(field.getType(), child, currentFieldPath, categoryPath);
                continue;
            }

            if (field.isAnnotationPresent(ConfigEntry.class)) {
                ConfigEntry entry = field.getAnnotation(ConfigEntry.class);

                String categoryPath;

                if (!entry.category().isEmpty()) {
                    categoryPath = parentCategory.isEmpty() ? entry.category() : parentCategory + "." + entry.category();
                }
                else {
                    categoryPath = parentCategory.isEmpty() ? "General" : parentCategory;
                }

                CategoryNode category = ensureCategoryPath(categoryPath);

                ConfigOption option = new ConfigOption(this, field, instance, currentFieldPath, category.key(), category.fallback());

                options.add(option);
                optionsByPath.put(currentFieldPath, option);
            }
        }
    }

    private Object getFieldValueForParsing(Field field, Object instance) {
        try {
            if (Modifier.isStatic(field.getModifiers())) {
                return field.get(null);
            }

            return field.get(instance);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to access config field " + field.getDeclaringClass().getName() + "." + field.getName(), e);
        }
    }

    private CategoryNode ensureCategoryPath(String path) {
        String[] parts = path.split("\\.");
        String currentPath = "";

        CategoryNode parent = null;
        for (String part : parts) {
            if (part.isEmpty()) {
                continue;
            }
            currentPath = currentPath.isEmpty() ? part : currentPath + "." + part;

            CategoryNode node = categories.get(currentPath);

            if (node == null) {
                String parentPath = currentPath.contains(".") ? currentPath.substring(0, currentPath.lastIndexOf('.')) : "";
                String key = TranslationUtil.translationKey("config", modId, type.name().toLowerCase(Locale.ROOT), "category", currentPath.toLowerCase(Locale.ROOT));

                int depth = parentPath.isEmpty() ? 0 : parentPath.split("\\.").length;
                node = new CategoryNode(currentPath, parentPath, key, humanize(part), depth);

                categories.put(currentPath, node);
                categoryPathByKey.put(key, currentPath);
            }

            parent = node;
        }

        return parent;
    }

    /**
     * Returns every category in declaration order.
     */
    public List<String> getCategoryKeys() {
        return categories.values().stream().map(CategoryNode::key).toList();
    }

    /**
     * Returns only root-level categories.
     */
    public List<String> getRootCategoryKeys() {
        return categories.values().stream().filter(node -> node.parentPath().isEmpty()).map(CategoryNode::key).toList();
    }

    /**
     * Returns direct children of a category.
     */
    public List<String> getCategoryChildren(String categoryKey) {
        CategoryNode node = getCategory(categoryKey);

        if (node == null) {
            return List.of();
        }

        return categories.values().stream().filter(child -> child.parentPath().equals(node.path())).map(CategoryNode::key).toList();
    }

    /**
     * Returns the category represented by a GUI category key.
     */
    public CategoryNode getCategory(String categoryKey) {
        String path = categoryPathByKey.get(categoryKey);

        return path == null ? null : categories.get(path);
    }

    /**
     * Returns the parent category key, or null for root categories.
     */
    public String getCategoryParent(String categoryKey) {
        CategoryNode node = getCategory(categoryKey);

        if (node == null || node.parentPath().isEmpty()) {
            return null;
        }

        CategoryNode parent = categories.get(node.parentPath());

        return parent == null ? null : parent.key();
    }

    /**
     * Returns whether this category has direct children.
     */
    public boolean hasCategoryChildren(String categoryKey) {
        CategoryNode node = getCategory(categoryKey);

        if (node == null) {
            return false;
        }

        return categories.values().stream().anyMatch(child -> child.parentPath().equals(node.path()));
    }

    public String getCategoryFallbackName(String categoryKey) {
        CategoryNode node = getCategory(categoryKey);

        return node != null ? node.fallback() : categoryKey;
    }

    /**
     * Returns options directly belonging to this category
     * and every descendant category.
     */
    public List<ConfigOption> getOptionsInCategory(String categoryKey) {
        CategoryNode category = getCategory(categoryKey);

        if (category == null) {
            return List.of();
        }

        String categoryPath = category.path();

        String prefix = categoryPath + ".";

        List<ConfigOption> result = new ArrayList<>();

        for (ConfigOption option : options) {
            String optionCategoryPath = option.categoryPath;

            if (optionCategoryPath.equals(categoryPath) || optionCategoryPath.startsWith(prefix)) {

                result.add(option);
            }
        }

        return result;
    }

    @ApiStatus.Internal
    public Object getFieldValue(Field field, Object owner, String path) {
        try {
            if (Modifier.isStatic(field.getModifiers())) {
                if (detached) {
                    return staticValues.get(path);
                }

                return field.get(null);
            }

            return field.get(owner);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to read config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'", e);
        }
    }

    @ApiStatus.Internal
    public void setFieldValue(Field field, Object owner, String path, Object value) {
        try {
            if (Modifier.isStatic(field.getModifiers())) {
                if (detached) {
                    staticValues.put(path, value);
                    return;
                }

                if (Modifier.isFinal(field.getModifiers())) {
                    throw new IllegalStateException("Cannot replace final static config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'.");
                }

                field.set(null, value);
                return;
            }

            field.set(owner, value);
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to set config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'", e);
        }
    }

    private Object resolveFieldValue(Field field, Object owner, String path) {
        try {
            if (!Modifier.isStatic(field.getModifiers())) {
                return field.get(owner);
            }
            if (!detached) {
                return field.get(null);
            }

            if (staticValues.containsKey(path)) {
                return staticValues.get(path);
            }

            Object original = field.get(null);
            Object copy = XunConfigManager.copyConfigValue(original, field.getGenericType(), field.isAnnotationPresent(ConfigGroup.class));

            staticValues.put(path, copy);

            return copy;
        }
        catch (IllegalAccessException e) {
            throw new IllegalStateException("Failed to resolve config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'", e);
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    @ApiStatus.Internal
    public void activateServer(MinecraftServer server, Path path) {
        this.server = server;
        this.path = path;
    }

    @ApiStatus.Internal
    public void deactivateServer(MinecraftServer server) {
        if (this.server == server) {
            this.server = null;
            this.path = basePath;
        }
    }

    public ConfigDefinition createEditorCopy() {
        Object copy = XunConfigManager.copyConfigInstance(instance, configClass);

        return new ConfigDefinition(modId, type, basePath, copy, configClass, true);
    }

    @ApiStatus.Internal
    public void applyValuesFrom(ConfigDefinition source) {
        if (source == null) {
            return;
        }

        if (!configClass.equals(source.configClass)) {
            throw new IllegalArgumentException("Cannot copy config values between different " + "config classes: " + configClass.getName() + " and " + source.configClass.getName());
        }

        XunConfigManager.copyConfigValues(this, source, configClass, instance, source.instance, "");
    }

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

        if (relativePath != null && !relativePath.isEmpty()) {

            ConfigOption relative = optionsByPath.get(relativePath + "." + field);

            if (relative != null) {
                return relative;
            }
        }

        return optionsByPath.get(field);
    }

    private static String joinPath(String parent, String child) {
        return parent.isEmpty() ? child : parent + "." + child;
    }

    private static String humanize(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        String[] words = text.replace('-', '_').split("_");

        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty()) {
                continue;
            }

            if (!result.isEmpty()) {
                result.append(' ');
            }

            result.append(Character.toUpperCase(word.charAt(0)));

            if (word.length() > 1) {
                result.append(word.substring(1));
            }
        }

        return result.toString();
    }

    /**
     * Public category tree node used by GUI implementations.
     */
    public record CategoryNode(String path, String parentPath, String key, String fallback, int depth) {}
}