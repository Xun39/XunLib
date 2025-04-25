package net.xun.lib.neoforge.internal.item.tools;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.misc.ModIDManager;
import net.xun.lib.common.internal.platform.RegistrationServices;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Internal NeoForge implementation of item registration using deferred registers.
 * Not part of the public API - use through {@link RegistrationServices} infrastructure.
 */
@ApiStatus.Internal
public class NeoForgeItemRegistrar implements ItemRegistrar {

    private static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ModIDManager.getModId());

    /**
     * {@inheritDoc}
     * <P> Registers with NeoForge's deferred registry system
     */
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> supplier) {
        REGISTRY.register(name, supplier);
        return supplier;
    }

    /**
     * Internal initialization method for binding registry to event bus
     * @param modEventBus The mod's primary event bus
     */
    public static void registerToEventBus(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
