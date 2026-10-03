package net.xun.lib.neoforge.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.fml.loading.FMLPaths;
import net.xun.lib.common.api.registration.Register;
import net.xun.lib.common.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.xun.lib.neoforge.api.registration.NeoForgeRegister;
import org.jetbrains.annotations.ApiStatus;

import java.nio.file.Path;

@ApiStatus.Internal
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T> Register<T> createRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new NeoForgeRegister<>(registry, namespace);
    }

    @Override
    public Register.Blocks createBlockRegister(String namespace) {
        return new NeoForgeRegister.Blocks(namespace);
    }

    @Override
    public Register.Items createItemRegister(String namespace) {
        return new NeoForgeRegister.Items(namespace);
    }
}