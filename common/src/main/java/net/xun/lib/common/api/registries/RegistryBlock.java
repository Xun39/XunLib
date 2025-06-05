package net.xun.lib.common.api.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/**
 * A specialized registry holder implementation for blocks that implements {@link ItemLike}.
 * <p>
 * This class provides additional functionality specific to blocks, including conversion
 * to item stacks and automatic handling of block-to-item relationships.
 * </p>
 *
 * @param <T> The concrete block type (must extend {@link Block})
 */
public class RegistryBlock<T extends Block> extends RegistryHolder<Block, T> implements ItemLike {

    /**
     * Creates an {@link ItemStack} of this block with a count of 1.
     *
     * @return The created item stack
     * @throws IllegalStateException If the block doesn't have a corresponding item
     */
    public ItemStack toStack() {
        return this.toStack(1);
    }

    /**
     * Creates an {@link ItemStack} of this block with the specified count.
     *
     * @param count The stack size
     * @return The created item stack
     * @throws IllegalStateException If the block doesn't have a corresponding item
     */
    public ItemStack toStack(int count) {
        ItemStack stack = this.asItem().getDefaultInstance();
        if (stack.isEmpty()) throw new IllegalStateException("Block does not have a corresponding item: " + this.key);
        stack.setCount(count);
        return stack;
    }

    /**
     * Creates a new block registry holder using a {@link ResourceLocation}.
     *
     * @param <T>      The concrete block type
     * @param key      The resource location identifying the block
     * @param supplier Supplier providing the block instance
     * @return The created registry holder
     */
    public static <T extends Block> RegistryBlock<T> createBlock(ResourceLocation key, Supplier<T> supplier) {
        return createBlock(ResourceKey.create(Registries.BLOCK, key), supplier);
    }

    /**
     * Creates a new block registry holder using a {@link ResourceKey}.
     *
     * @param <T>      The concrete block type
     * @param key      The resource key identifying the block
     * @param supplier Supplier providing the block instance
     * @return The created registry holder
     */
    public static <T extends Block> RegistryBlock<T> createBlock(ResourceKey<Block> key, Supplier<T> supplier) {
        return new RegistryBlock<T>(key, supplier);
    }

    /**
     * Constructs a new block registry holder.
     *
     * @param key      The resource key identifying the block
     * @param supplier Supplier providing the block instance
     */
    protected RegistryBlock(ResourceKey<Block> key, Supplier<T> supplier) {
        super(key, supplier);
    }

    /**
     * Gets the corresponding item for this block.
     *
     * @return The item representation of this block
     */
    @Override
    public Item asItem() {
        return get().asItem();
    }

    /**
     * Binds this holder to a concrete registry holder.
     *
     * @param holder The concrete holder from the registry
     */
    @Override
    public void bind(Holder<Block> holder) {
        super.bind(holder);
    }
}
