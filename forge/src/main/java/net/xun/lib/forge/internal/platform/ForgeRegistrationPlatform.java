package net.xun.lib.forge.internal.platform;

import net.minecraft.core.Registry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.IForgeRegistry;
import net.xun.lib.common.internal.platform.IRegistrationPlatform;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.forge.api.registries.ForgeRegistrar;
import net.xun.lib.forge.api.registries.ForgeRegistryHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ApiStatus.Internal
public class ForgeRegistrationPlatform implements IRegistrationPlatform {

    public static Map<IForgeRegistry<?>, ForgeRegistrar<?>> map = new HashMap<>();

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry, String modId) {
        IForgeRegistry<T> forgeRegistry = ForgeRegistryHelper.getForgeRegistry(registry);
        ForgeRegistrar<T> registrar = new ForgeRegistrar<>(forgeRegistry, modId);
        map.put(forgeRegistry, registrar);
        return registrar;
    }

    public static Collection<ForgeRegistrar<?>> getRegistrars() {
        return map.values();
    }

    public static void registerAllToEventBus(IEventBus eventBus) {
        for (ForgeRegistrar<?> registrar : getRegistrars()) {
            registrar.register(eventBus);
        }
    }
}
