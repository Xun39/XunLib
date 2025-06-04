package net.xun.lib.fabric.internal.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.internal.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.xun.lib.fabric.api.registries.FabricRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Registrar<T> createRegistrar(ResourceKey<Registry<T>> registry, String namespace) {
        return new FabricRegistrar<>(registry);
    }
}
