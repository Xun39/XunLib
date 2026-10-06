package net.xun.lib.common.api.config;

import net.minecraft.network.chat.Component;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.util.TranslationUtil;

import java.lang.reflect.Field;

public class ConfigOption {
    public final String fieldName;
    public final String path;
    public final Field field;
    public final Object instance;
    public final ConfigDefinition holder;
    public final Class<?> type;

    public final String categoryKey;
    public final String categoryFallback;

    public final String nameKey;
    public final String nameFallback;

    public final String descriptionKey;
    public final String descriptionFallback;

    public final String dependsOnField;
    public final String requiredValue;

    public final double minValue;
    public final double maxValue;
    public final double stepValue;

    public ConfigOption(ConfigDefinition holder, Field field, Object instance, String path, String inheritedCategory) {
        this.holder = holder;
        this.field = field;
        this.instance = instance;
        this.path = path;

        this.fieldName = field.getName();
        this.type = field.getType();

        field.setAccessible(true);

        ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
        ConfigDescription description = field.getAnnotation(ConfigDescription.class);
        VisibleWhen dependsOn = field.getAnnotation(VisibleWhen.class);

        // Category
        String rawCategory;

        if (entry != null && !entry.category().isEmpty()) {
            rawCategory = entry.category();
        }
        else if (inheritedCategory != null && !inheritedCategory.isEmpty()) {
            rawCategory = inheritedCategory;
        }
        else {
            rawCategory = "General";
        }

        this.categoryFallback = rawCategory;
        this.categoryKey = rawCategory.contains(".") ? rawCategory : TranslationUtil.translationKey("config", holder.modId, holder.type.name().toLowerCase(), "category", rawCategory.toLowerCase());

        // Option Name
        String rawName = entry != null && !entry.name().isEmpty() ? entry.name() : fieldName;

        this.nameKey = rawName.contains(".") ? rawName : TranslationUtil.translationKey("config", holder.modId, "option", path);
        this.nameFallback = rawName;

        // Description
        if (description != null && !description.value().isEmpty()) {
            this.descriptionKey = description.value().contains(".") ? description.value() : TranslationUtil.translationKey("config", holder.modId, "option", path, "description");
            this.descriptionFallback = description.value();
        }
        else {
            this.descriptionKey = TranslationUtil.translationKey("config", holder.modId, "option", path, "description");
            this.descriptionFallback = "No description provided.";
        }

        // Dependency
        if (dependsOn != null) {
            this.dependsOnField = dependsOn.field();
            this.requiredValue = dependsOn.is();
        }
        else {
            this.dependsOnField = null;
            this.requiredValue = null;
        }

        // Range
        this.minValue = entry != null ? entry.min() : Double.NEGATIVE_INFINITY;
        this.maxValue = entry != null ? entry.max() : Double.POSITIVE_INFINITY;
        this.stepValue = entry != null ? entry.step() : 0.0;
    }

    public Number getMin() {
        return minValue != Double.NEGATIVE_INFINITY ? minValue : null;
    }

    public Number getMax() {
        return maxValue != Double.POSITIVE_INFINITY ? maxValue : null;
    }

    public Number getStep() {
        return stepValue > 0.0 ? stepValue : null;
    }

    public Component getDisplayName() {
        return Component.translatableWithFallback(nameKey, nameFallback);
    }

    public Component getDescription() {
        return Component.translatableWithFallback(descriptionKey, descriptionFallback);
    }

    public Object getValue() {
        try {
            return field.get(instance);
        }
        catch (IllegalAccessException e) {
            XunLibConstants.LOGGER.error("Failed to read config field '{}.{}'", field.getDeclaringClass().getName(), field.getName(), e);
            return null;
        }
    }

    public void setValue(Object value) {
        try {
            field.set(instance, value);
        }
        catch (IllegalAccessException e) {
            XunLibConstants.LOGGER.error("Failed to set config field '{}.{}' to value '{}'", field.getDeclaringClass().getName(), field.getName(), value, e);
        }
    }

    /**
     * Recursively checks whether parent dependencies are satisfied.
     */
    public boolean isVisible() {
        if (dependsOnField == null || dependsOnField.isEmpty()) {
            return true;
        }

        ConfigOption dependency = holder.findOption(dependsOnField, getParentPath());

        if (dependency == null) {
            return true;
        }

        if (!dependency.isVisible()) {
            return false;
        }

        Object value = dependency.getValue();

        if (value == null) {
            return false;
        }

        return String.valueOf(value).equalsIgnoreCase(requiredValue);
    }

    private String getParentPath() {
        int separator = path.lastIndexOf('.');
        return separator >= 0 ? path.substring(0, separator) : "";
    }
}