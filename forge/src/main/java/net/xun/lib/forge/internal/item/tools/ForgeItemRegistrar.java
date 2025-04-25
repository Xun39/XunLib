package net.xun.lib.forge.internal.item.tools;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.misc.ModIDManager;
import net.xun.lib.common.internal.platform.RegistrationServices;

import java.util.function.Supplier;

/**
 * Internal Forge implementation of item registration using deferred registers.
 * Not part of the public API - use through {@link RegistrationServices} infrastructure.
 */
public class ForgeItemRegistrar implements ItemRegistrar {

    private static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, ModIDManager.getModId());

    /**
     * {@inheritDoc}
     * <P> Registers with Forge's deferred registry system
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
    public static void setRegistry(IEventBus modEventBus) {
        REGISTRY.register(modEventBus);
    }
}
