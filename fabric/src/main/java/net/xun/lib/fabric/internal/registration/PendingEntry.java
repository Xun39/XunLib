package net.xun.lib.fabric.internal.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registration.RegistryEntry;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

@ApiStatus.Internal
record PendingEntry<T, R extends T>(ResourceKey<T> key, Supplier<? extends R> factory, RegistryEntry<T, R> entry) {
    void register(Registry<T> registry) {
        R value = Objects.requireNonNull(factory.get(), "Registration factory returned null for " + key);
        R registeredValue = Registry.register(registry, key, value);

        // Bind to the actual registered instance
        entry.bind(() -> registeredValue);
    }
}
