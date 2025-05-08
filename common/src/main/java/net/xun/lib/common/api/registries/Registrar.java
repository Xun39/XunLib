package net.xun.lib.common.api.registries;

import net.minecraft.core.Registry;
import net.xun.lib.common.internal.platform.IRegistrationPlatform;
import net.xun.lib.common.internal.platform.Services;

import java.util.function.Supplier;

public interface Registrar<T> {

    /**
     * Factory method to create a registrar for the given registry.
     */
    static <T> Registrar<T> create(Registry<T> registry, String modId) {
        IRegistrationPlatform platform = Services.load(IRegistrationPlatform.class);
        return platform.createRegistrar(registry, modId);
    }

    /**
     * Register an entry with lazy initialization
     */
    <U extends T> LazyRegistryReference<U> register(String name, Supplier<U> supplier);
}
