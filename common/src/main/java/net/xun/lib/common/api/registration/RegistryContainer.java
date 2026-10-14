package net.xun.lib.common.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.platform.Services;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class RegistryContainer {
    private static final Map<String, RegistryContainer> INSTANCES = new ConcurrentHashMap<>();

    public static RegistryContainer of(String namespace) {
        return INSTANCES.computeIfAbsent(namespace, RegistryContainer::new);
    }

    private final String namespace;
    private final Map<ResourceKey<?>, Registrar<?>> registrars = new LinkedHashMap<>();

    private final Map<RegistrationEvent<?>, List<RegistrationCallback<?>>> callbacks = new LinkedHashMap<>();

    private RegistryContainer(String namespace) {
        this.namespace = namespace;
    }

    public String namespace() {
        return namespace;
    }

    public <T, R extends T> Supplier<R> register(ResourceKey<? extends Registry<T>> registryKey, String name, Supplier<R> supplier) {
        return registrarOf(registryKey).register(name, supplier);
    }

    public <I extends Item> Supplier<I> registerItem(String name, Supplier<I> supplier) {
        return items().register(name, supplier);
    }

    public <B extends Block> Supplier<B> registerBlock(String name, Supplier<B> supplier) {
        return blocks().register(name, supplier);
    }

    @SuppressWarnings("unchecked")
    public synchronized <T> Registrar<T> registrarOf(ResourceKey<? extends Registry<T>> registryKey) {
        Objects.requireNonNull(registryKey, "registryKey");

        if (Registries.BLOCK.equals(registryKey)) {
            return (Registrar<T>) blocks();
        }
        if (Registries.ITEM.equals(registryKey)) {
            return (Registrar<T>) items();
        }

        return (Registrar<T>) getOrCreate(registryKey, () -> Services.REGISTRATION.createRegistrar(registryKey, namespace));
    }

    public synchronized Registrar.Blocks blocks() {
        Registrar.Items items = items();

        return (Registrar.Blocks) getOrCreate(Registries.BLOCK, () -> Services.REGISTRATION.createBlockRegistrar(namespace, items));
    }

    public synchronized Registrar.Items items() {
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
        for (Registrar<?> registrar : registrationOrder()) {
            registrar.registerEntries();
        }
    }

    // This is important because registerWithItem method in Registrar.Blocks calls itemRegistrar.register,
    // therefore item registration depends on the block that need to be registered first.
    private List<Registrar<?>> registrationOrder() {
        List<Registrar<?>> ordered = new ArrayList<>(registrars.size());

        Registrar<?> blocks = registrars.get(Registries.BLOCK);
        Registrar<?> items = registrars.get(Registries.ITEM);

        if (blocks != null) ordered.add(blocks);
        if (items != null) ordered.add(items);

        for (Map.Entry<ResourceKey<?>, Registrar<?>> entry : registrars.entrySet()) {
            ResourceKey<?> key = entry.getKey();
            if (!Registries.BLOCK.equals(key) && !Registries.ITEM.equals(key)) {
                ordered.add(entry.getValue());
            }
        }
        return ordered;
    }

    public synchronized <C> RegistryContainer on(RegistrationEvent<C> event, RegistrationCallback<? super C> callback) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(callback, "callback");

        callbacks.computeIfAbsent(event, ignored -> new ArrayList<>()).add(callback);

        return this;
    }

    @ApiStatus.Internal
    public static <C> void dispatchCallbacks(RegistrationEvent<C> event, C context) {
        Objects.requireNonNull(event, "event");
        Objects.requireNonNull(context, "context");

        for (RegistryContainer container : List.copyOf(INSTANCES.values())) {
            container.dispatchLocal(event, context);
        }
    }

    @SuppressWarnings("unchecked")
    private <C> void dispatchLocal(RegistrationEvent<C> event, C context) {
        List<RegistrationCallback<?>> snapshot;

        synchronized (this) {
            snapshot = List.copyOf(callbacks.getOrDefault(event, List.of()));
        }

        for (RegistrationCallback<?> callback : snapshot) {
            ((RegistrationCallback<C>) callback).register(context);
        }
    }
}
