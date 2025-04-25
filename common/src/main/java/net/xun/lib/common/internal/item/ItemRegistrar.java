package net.xun.lib.common.internal.item;

import net.minecraft.world.item.Item;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;

/**
 * Internal API for registering items with the mod registry system.
 */
@ApiStatus.Internal
public interface ItemRegistrar {

    /**
     * Registers a item with the mod registry.
     *
     * @param <T> Type of item being registered
     * @param registryName Unique registry name for the item (id)
     * @param supplier Factory for creating the instance
     * @return Registered item supplier that can resolve the instance
     */
    <T extends Item> Supplier<T> registerItem(String registryName, Supplier<T> supplier);
}
