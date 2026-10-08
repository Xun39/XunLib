package net.xun.lib.common.api.config;

import net.minecraft.server.MinecraftServer;
import net.xun.lib.common.internal.config.XunConfigIO;
import net.xun.lib.common.internal.config.XunConfigRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Map;

public final class XunConfigManager {
    private XunConfigManager() {
    }

    public static <T> T registerConfig(Class<T> configClass) {
        return XunConfigRegistry.register(configClass);
    }

    public static void loadServerConfigs(MinecraftServer server) {
        XunConfigRegistry.loadServerConfigs(server);
    }

    public static void unloadServerConfigs(MinecraftServer server) {
        XunConfigRegistry.unloadServerConfigs(server);
    }

    public static MinecraftServer getCurrentServer() {
        return XunConfigRegistry.getCurrentServer();
    }

    public static boolean hasLogicalServer() {
        return XunConfigRegistry.hasLogicalServer();
    }

    public static ConfigDefinition getConfig(String modId, ConfigType type) {
        return XunConfigRegistry.get(modId, type);
    }

    public static Map<ConfigType, ConfigDefinition> getConfigs(String modId) {
        return XunConfigRegistry.getAll(modId);
    }

    public static boolean canEdit(ConfigDefinition config) {
        return XunConfigRegistry.canEdit(config);
    }

    public static void saveConfig(String modId, ConfigType type) {
        XunConfigRegistry.save(modId, type);
    }

    public static void saveConfig(ConfigDefinition config) {
        XunConfigRegistry.save(config);
    }

    public static void saveEditedConfig(ConfigDefinition target, ConfigDefinition edited) {
        XunConfigRegistry.saveEdited(target, edited);
    }

    public static void saveConfigs(String modId) {
        XunConfigRegistry.saveAll(modId);
    }

    public static void setDirectoryMode(ConfigDirectoryMode mode) {
        XunConfigIO.setDirectoryMode(mode);
    }

    public static ConfigDirectoryMode getDirectoryMode() {
        return XunConfigIO.getDirectoryMode();
    }

    static Object copyConfigInstance(Object instance, Class<?> configClass) {
        return XunConfigIO.copyConfigInstance(instance, configClass);
    }

    static Object copyConfigValue(Object value, Type type, boolean group) {
        return XunConfigIO.copyConfigValue(value, type, group);
    }

    static void copyConfigValues(ConfigDefinition targetDefinition, ConfigDefinition sourceDefinition, Class<?> configClass, Object target, Object source, String parentPath) {
        for (Field field : configClass.getDeclaredFields()) {
            int modifiers = field.getModifiers();

            if (ModifierHelper.isTransientOrIgnored(modifiers)) {
                continue;
            }
            field.setAccessible(true);

            String path = parentPath.isEmpty() ? field.getName() : parentPath + "." + field.getName();
            try {
                ConfigGroup group = field.getAnnotation(ConfigGroup.class);

                if (group != null) {
                    Object sourceChild = sourceDefinition.getFieldValue(field, source, path);
                    Object targetChild = targetDefinition.getFieldValue(field, target, path);

                    if (sourceChild == null) {
                        continue;
                    }

                    if (targetChild == null) {
                        if (targetDefinition.isDetached()) {
                            targetDefinition.setFieldValue(field, target, path, copyConfigValue(sourceChild, field.getGenericType(), true));
                        }
                        else if (!Modifier.isFinal(modifiers)) {
                            targetDefinition.setFieldValue(field, target, path, copyConfigValue(sourceChild, field.getGenericType(), true));
                        }

                        continue;
                    }

                    copyConfigValues(targetDefinition, sourceDefinition, field.getType(), targetChild, sourceChild, path);
                    continue;
                }

                if (field.isAnnotationPresent(ConfigEntry.class)) {
                    Object value = sourceDefinition.getFieldValue(field, source, path);
                    Object copy = copyConfigValue(value, field.getGenericType(), false);
                    targetDefinition.setFieldValue(field, target, path, copy);
                }
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to copy config field '" + field.getDeclaringClass().getName() + "." + field.getName() + "'", e);
            }
        }
    }

    private static final class ModifierHelper {
        private ModifierHelper() {
        }

        static boolean isTransientOrIgnored(int modifiers) {
            return Modifier.isTransient(modifiers);
        }
    }
}