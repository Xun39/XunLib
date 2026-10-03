package net.xun.lib.common.api.config;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.*;

public class ConfigDefinition {
    public final String modId;
    public final Path path;
    public final Object instance;
    public final Class<?> configClass;

    private final List<ConfigOption> options = new ArrayList<>();
    private final Map<String, ConfigOption> optionsByFieldName = new HashMap<>();

    public ConfigDefinition(String modId, Path path, Object instance, Class<?> configClass) {
        this.modId = modId;
        this.path = path;
        this.instance = instance;
        this.configClass = configClass;
        parseFields();
    }

    private void parseFields() {
        for (Field field : configClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigEntry.class)) {
                ConfigOption opt = new ConfigOption(this, field, instance);
                options.add(opt);
                optionsByFieldName.put(field.getName(), opt);
            }
        }
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