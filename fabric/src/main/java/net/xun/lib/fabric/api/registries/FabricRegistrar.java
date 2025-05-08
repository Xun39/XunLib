package net.xun.lib.fabric.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.xun.lib.common.api.registries.LazyRegistryReference;
import net.xun.lib.common.api.registries.Registrar;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class FabricRegistrar<T> implements Registrar<T> {
    private final Registry<T> registry;
    private final String modId;
    private final Set<String> registeredNames = ConcurrentHashMap.newKeySet();

    public FabricRegistrar(Registry<T> registry, String modId) {
        this.registry = registry;
        this.modId = modId;
    }

    @Override
    public <U extends T> LazyRegistryReference<U> register(String name, Supplier<U> supplier) {
        if (!registeredNames.add(name)) {
            throw new IllegalArgumentException("Duplicate registration: " + name);
        }

        LazyRegistryReference<U> ref = new LazyRegistryReference<>(name, supplier);

        Registry.register(registry, ResourceLocation.fromNamespaceAndPath(modId, name), supplier.get());
        return ref;
    }
}
