package net.xun.lib.common.api.config;

import net.minecraft.network.chat.Component;
import net.xun.lib.common.api.util.TranslationUtil;

import java.lang.reflect.Field;

public class ConfigOption {
    public final String fieldName;
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

    public ConfigOption(ConfigDefinition holder, Field field, Object instance) {
        this.holder = holder;
        this.field = field;
        this.instance = instance;
        this.fieldName = field.getName();
        this.type = field.getType();
        this.field.setAccessible(true);

        ConfigEntry entry = field.getAnnotation(ConfigEntry.class);
        ConfigDescription comment = field.getAnnotation(ConfigDescription.class);
        VisibleWhen dependsOn = field.getAnnotation(VisibleWhen.class);

        // Category resolution
        String catRaw = (entry != null && !entry.category().isEmpty()) ? entry.category() : "General";
        this.categoryFallback = catRaw;
        this.categoryKey = catRaw.contains(".")
                ? catRaw
                : TranslationUtil.translationKey("config", holder.modId, "category", catRaw.toLowerCase());

        // Option name resolution
        String nameRaw = (entry != null && !entry.name().isEmpty()) ? entry.name() : fieldName;
        this.nameKey = nameRaw.contains(".")
                ? nameRaw
                : TranslationUtil.translationKey("config", holder.modId, "option", fieldName);
        this.nameFallback = nameRaw;

        // Comment/tooltip resolution
        if (comment != null && !comment.value().isEmpty()) {
            this.descriptionKey = comment.value().contains(".")
                    ? comment.value()
                    : TranslationUtil.translationKey("config", holder.modId, "option", fieldName, "comment");
            this.descriptionFallback = comment.value();
        }
        else {
            this.descriptionKey = TranslationUtil.translationKey("config", holder.modId, "option", fieldName, "comment");
            this.descriptionFallback = "No description provided.";
        }

        // Dependency resolution
        if (dependsOn != null) {
            this.dependsOnField = dependsOn.field();
            this.requiredValue = dependsOn.is();
        }
        else {
            this.dependsOnField = null;
            this.requiredValue = null;
        }

        this.minValue = (entry != null) ? entry.min() : Double.NEGATIVE_INFINITY;
        this.maxValue = (entry != null) ? entry.max() : Double.POSITIVE_INFINITY;
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
            return null;
        }
    }

    public void setValue(Object value) {
        try {
            field.set(instance, value);
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recursively checks if parent dependencies are met
     */
    public boolean isVisible() {
        if (dependsOnField == null || dependsOnField.isEmpty()) {
            return true;
        }
        ConfigOption dependencyOption = holder.findOption(dependsOnField);
        if (dependencyOption == null) return true;

        if (!dependencyOption.isVisible()) return false;

        Object val = dependencyOption.getValue();
        if (val == null) return false;

        return String.valueOf(val).equalsIgnoreCase(requiredValue);
    }
}