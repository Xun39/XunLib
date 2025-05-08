package net.xun.lib.common.api.registries;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class LazyItemReference<T extends Item> extends LazyRegistryReference<T> implements ItemLike {

    public LazyItemReference(String name, Supplier<T> supplier) {
        super(name, supplier);
    }

    @Override
    public @NotNull Item asItem() {
        return this.get();
    }
}
