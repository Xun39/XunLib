package net.xun.lib.common.api.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

public class RegistryItem<T extends Item> extends RegistryHolder<Item, T> implements ItemLike {

    public ItemStack toStack() {
        return this.toStack(1);
    }

    public ItemStack toStack(int count) {
        ItemStack stack = this.asItem().getDefaultInstance();
        if (stack.isEmpty()) {
            throw new IllegalStateException("Obtained empty item stack; incorrect getDefaultInstance() call?");
        } else {
            stack.setCount(count);
            return stack;
        }
    }

    public static <T extends Item> RegistryItem<T> createItem(ResourceLocation key, Supplier<T> supplier) {
        return createItem(ResourceKey.create(Registries.ITEM, key), supplier);
    }

    public static <T extends Item> RegistryItem<T> createItem(ResourceKey<Item> key, Supplier<T> supplier) {
        return new RegistryItem<T>(key, supplier);
    }

    protected RegistryItem(ResourceKey<Item> key, Supplier<T> supplier) {
        super(key, supplier);
    }

    @Override
    public Item asItem() {
        return (Item) this.get();
    }
}
