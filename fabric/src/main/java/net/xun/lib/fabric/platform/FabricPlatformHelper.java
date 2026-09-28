package net.xun.lib.fabric.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registration.Register;
import net.xun.lib.common.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.xun.lib.fabric.api.registration.FabricRegister;
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
    public <T> Register<T> createRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new FabricRegister<>(registry, namespace);
    }

    @Override
    public Register.Blocks createBlockRegister(String namespace) {
        return new FabricRegister.FabricBlocks(namespace);
    }

    @Override
    public Register.Items createItemRegister(String namespace) {
        return new FabricRegister.FabricItems(namespace);
    }
}
