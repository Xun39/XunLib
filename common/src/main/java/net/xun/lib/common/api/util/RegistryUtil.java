package net.xun.lib.common.api.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Optional;

public final class RegistryUtil {
    private RegistryUtil() {
    }

    public static <T> Optional<ResourceKey<T>> getKey(Holder<T> holder) {
        return holder.unwrapKey();
    }

    public static <T> Optional<ResourceLocation> getId(Holder<T> holder) {
        return getKey(holder).map(ResourceKey::location);
    }

    public static <T> Optional<ResourceKey<T>> getKey(Registry<T> registry, T value) {
        return registry.getResourceKey(value);
    }

    public static <T> Optional<ResourceLocation> getId(Registry<T> registry, T value) {
        return registry.getResourceKey(value).map(ResourceKey::location);
    }
}
