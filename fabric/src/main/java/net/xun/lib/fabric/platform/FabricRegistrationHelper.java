package net.xun.lib.fabric.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.platform.services.IRegistrationHelper;
import net.xun.lib.fabric.internal.registration.FabricRegistrar;

public class FabricRegistrationHelper implements IRegistrationHelper {
    @Override
    public <T> Registrar<T> createRegistrar(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new FabricRegistrar<>(registry, namespace);
    }

    @Override
    public Registrar.Blocks createBlockRegistrar(String namespace, Registrar.Items itemRegistrar) {
        return new FabricRegistrar.Blocks(namespace, itemRegistrar);
    }

    @Override
    public Registrar.Items createItemRegistrar(String namespace) {
        return new FabricRegistrar.Items(namespace);
    }
}
