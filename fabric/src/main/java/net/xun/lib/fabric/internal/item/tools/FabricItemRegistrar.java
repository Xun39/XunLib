package net.xun.lib.fabric.internal.item.tools;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.RegistrationServices;

import java.util.function.Supplier;

/**
 * Internal Fabric implementation of item registration using deferred registers.
 * Not part of the public API - use through {@link RegistrationServices} infrastructure.
 */
public class FabricItemRegistrar implements ItemRegistrar {

    /**
     * {@inheritDoc}
     * <P> Registers with Fabric's registry system
     */
    @Override
    public <T extends Item> Supplier<T> registerItem(String name, Supplier<T> supplier) {
        Registry.register(BuiltInRegistries.ITEM, CommonUtils.modResourceLocation(name), supplier.get());
        return supplier;
    }
}
