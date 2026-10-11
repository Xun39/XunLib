package net.xun.lib.neoforge.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.platform.services.IRegistrationHelper;
import net.xun.lib.neoforge.internal.registration.NeoForgeRegistrar;

public class NeoForgeRegistrationHelper implements IRegistrationHelper {
    @Override
    public <T> Registrar<T> createRegistrar(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new NeoForgeRegistrar<>(registry, namespace);
    }

    @Override
    public Registrar.Blocks createBlockRegistrar(String namespace, Registrar.Items itemRegistrar) {
        return new NeoForgeRegistrar.Blocks(namespace, itemRegistrar);
    }

    @Override
    public Registrar.Items createItemRegistrar(String namespace) {
        return new NeoForgeRegistrar.Items(namespace);
    }
}
