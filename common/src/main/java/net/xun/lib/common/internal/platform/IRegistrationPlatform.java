package net.xun.lib.common.internal.platform;

import net.minecraft.core.Registry;
import net.xun.lib.common.api.registries.Registrar;

public interface IRegistrationPlatform {

    <T> Registrar<T> createRegistrar(Registry<T> registry, String modId);
}
