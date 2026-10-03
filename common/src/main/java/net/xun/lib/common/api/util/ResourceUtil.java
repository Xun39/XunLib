package net.xun.lib.common.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.xun.lib.common.impl.ModIDManager;

public class ResourceUtil {
    private ResourceUtil() {
    }

    public static ResourceLocation modLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(ModIDManager.getModId(), path);
    }

    public static ResourceLocation modLoc(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static String namespacedID(String namespace, String... pathParts) {
        return "%s:%s".formatted(namespace, String.join("_", pathParts));
    }

    public static String namespacedID(String... pathParts) {
        return namespacedID(ModIDManager.getModId(), pathParts);
    }

    public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registryKey, String path) {
        return ResourceKey.create(registryKey, ResourceUtil.modLoc(path));
    }
}
