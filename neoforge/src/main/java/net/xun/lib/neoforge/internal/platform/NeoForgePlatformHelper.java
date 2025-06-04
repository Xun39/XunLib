package net.xun.lib.neoforge.internal.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.internal.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.xun.lib.neoforge.api.registries.NeoForgeRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
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
    public <T> Registrar<T> createRegistrar(ResourceKey<Registry<T>> registry, String namespace) {
        return new NeoForgeRegistrar<>(registry, namespace);
    }
}