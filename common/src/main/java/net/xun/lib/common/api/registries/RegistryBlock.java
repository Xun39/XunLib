package net.xun.lib.common.api.registries;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

public class RegistryBlock<T extends Block> extends RegistryHolder<Block, T> implements ItemLike {

    public ItemStack toStack() {
        return this.toStack(1);
    }

    public ItemStack toStack(int count) {
        ItemStack stack = this.asItem().getDefaultInstance();
        if (stack.isEmpty()) {
            throw new IllegalStateException("Block does not have a corresponding item: " + this.key);
        } else {
            stack.setCount(count);
            return stack;
        }
    }

    public static <T extends Block> RegistryBlock<T> createBlock(ResourceLocation key, Supplier<T> supplier) {
        return createBlock(ResourceKey.create(Registries.BLOCK, key), supplier);
    }

    public static <T extends Block> RegistryBlock<T> createBlock(ResourceKey<Block> key, Supplier<T> supplier) {
        return new RegistryBlock<T>(key, supplier);
    }

    protected RegistryBlock(ResourceKey<Block> key, Supplier<T> factory) {
        super(key, factory);
    }

    @Override
    public Item asItem() {
        return ((Block) this.get()).asItem();
    }
}
