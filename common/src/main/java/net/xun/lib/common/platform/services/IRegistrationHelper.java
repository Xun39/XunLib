package net.xun.lib.common.platform.services;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registration.Registrar;

public interface IRegistrationHelper {

    <T> Registrar<T> createRegistrar(ResourceKey<? extends Registry<T>> registry, String namespace);

    Registrar.Blocks createBlockRegistrar(String namespace, Registrar.Items itemRegistrar);

    Registrar.Items createItemRegistrar(String namespace);
}
