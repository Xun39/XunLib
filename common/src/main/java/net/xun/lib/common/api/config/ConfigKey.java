package net.xun.lib.common.api.config;

public record ConfigKey(String modId, ConfigType type) {
    public static ConfigKey of(String modId, ConfigType type) {
        return new ConfigKey(modId, type);
    }
}
