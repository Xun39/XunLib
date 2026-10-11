package net.xun.lib.common.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.platform.Services;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class RegistryContainer {
    private static final Map<String, RegistryContainer> INSTANCES = new ConcurrentHashMap<>();

    public static RegistryContainer of(String namespace) {
        return INSTANCES.computeIfAbsent(namespace, RegistryContainer::new);
    }

    private final String namespace;
    private final Map<ResourceKey<?>, Registrar<?>> registrars = new LinkedHashMap<>();

    private RegistryContainer(String namespace) {
        this.namespace = namespace;
    }

    public String namespace() {
        return namespace;
    }

    @SuppressWarnings("unchecked")
    public <T> Registrar<T> registrarOf(ResourceKey<? extends Registry<T>> registryKey) {
        return (Registrar<T>) getOrCreate(registryKey, () -> Services.REGISTRATION.createRegistrar(registryKey, namespace));
    }

    public Registrar.Blocks blocks() {
        Registrar.Items items = items();

        return (Registrar.Blocks) getOrCreate(Registries.BLOCK, () -> Services.REGISTRATION.createBlockRegistrar(namespace, items));
    }

    public Registrar.Items items() {
        return (Registrar.Items) getOrCreate(Registries.ITEM, () -> Services.REGISTRATION.createItemRegistrar(namespace));
    }

    private synchronized Registrar<?> getOrCreate(ResourceKey<?> key, Supplier<? extends Registrar<?>> factory) {
        Registrar<?> existing = registrars.get(key);
        if (existing != null) {
            return existing;
        }

        Registrar<?> created = factory.get();
        registrars.put(key, created);
        return created;
    }

    public void registerAll() {
        registrars.forEach((registryKey, registrar) -> {
            registrar.registerEntries();
        });
    }
}
