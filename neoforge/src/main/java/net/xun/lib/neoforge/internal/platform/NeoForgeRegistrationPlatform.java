package net.xun.lib.neoforge.internal.platform;

import net.minecraft.core.Registry;
import net.neoforged.bus.api.IEventBus;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.internal.platform.IRegistrationPlatform;
import net.xun.lib.neoforge.api.registries.NeoForgeRegistrar;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@ApiStatus.Internal
public class NeoForgeRegistrationPlatform implements IRegistrationPlatform {

    public static Map<Registry<?>, NeoForgeRegistrar<?>> map = new HashMap<>();

    @Override
    public <T> Registrar<T> createRegistrar(Registry<T> registry, String modId) {
        NeoForgeRegistrar<T> registrar = new NeoForgeRegistrar<>(registry, modId);
        map.put(registry, registrar);
        return registrar;
    }

    public static Collection<NeoForgeRegistrar<?>> getRegistrars() {
        return map.values();
    }

    public static void registerAllToEventBus(IEventBus eventBus) {
        for (NeoForgeRegistrar<?> registrar : getRegistrars()) {
            registrar.register(eventBus);
        }
    }
}
