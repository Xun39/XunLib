package net.xun.lib.fabric.internal.platform;

import net.minecraft.core.Registry;
import net.xun.lib.common.internal.platform.IRegistrationPlatform;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.fabric.api.registries.FabricRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricRegistrationPlatform implements IRegistrationPlatform {

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry, String modId) {
        return new FabricRegistrar<>(registry, modId);
    }
}
