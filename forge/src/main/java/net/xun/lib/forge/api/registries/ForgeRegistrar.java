package net.xun.lib.forge.api.registries;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.xun.lib.common.api.registries.LazyRegistryReference;
import net.xun.lib.common.api.registries.Registrar;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class ForgeRegistrar<T> implements Registrar<T> {
    private final DeferredRegister<T> deferredRegister;
    private final Set<String> registeredNames = ConcurrentHashMap.newKeySet();

    public ForgeRegistrar(IForgeRegistry<T> registry, String modId) {
        this.deferredRegister = DeferredRegister.create(registry, modId);
    }

    @Override
    public <U extends T> LazyRegistryReference<U> register(String name, Supplier<U> supplier) {
        if (!registeredNames.add(name)) {
            throw new IllegalArgumentException("Duplicate registration: " + name);
        }

        LazyRegistryReference<U> ref = new LazyRegistryReference<>(name, supplier);

        deferredRegister.register(name, supplier);
        return ref;
    }

    public void register(IEventBus eventBus) {
        deferredRegister.register(eventBus);
    }
}
