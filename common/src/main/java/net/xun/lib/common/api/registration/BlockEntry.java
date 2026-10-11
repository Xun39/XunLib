package net.xun.lib.common.api.registration;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockEntry<B extends Block> extends RegistryEntry<Block, B> implements ItemLike {
    protected BlockEntry(ResourceKey<Block> key, Supplier<Optional<Holder<Block>>> holderLookup) {
        super(key, holderLookup);
    }

    public static <B extends Block> BlockEntry<B> createBlock(ResourceKey<Block> key, Supplier<Optional<Holder<Block>>> holderLookup) {
        return new BlockEntry<>(key, holderLookup);
    }

    @Override
    public @NotNull Item asItem() {
        return get().asItem();
    }

    public ItemStack toStack() {
        return toStack(1);
    }

    public ItemStack toStack(int count) {
        ItemStack stack = this.asItem().getDefaultInstance();
        if (stack.isEmpty()) {
            throw new IllegalStateException("Obtained empty item stack; incorrect getDefaultInstance() call?");
        }
        stack.setCount(count);
        return stack;
    }
}
