package net.xun.lib.common.api.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.function.Supplier;

/**
 * A specialized registry holder implementation for items that implements {@link ItemLike}.
 * <p>
 * This class provides convenient methods for working with items, including creating
 * item stacks of varying sizes.
 * </p>
 *
 * @param <T> The concrete item type (must extend {@link Item})
 */
public class RegistryItem<T extends Item> extends RegistryHolder<Item, T> implements ItemLike {

    /**
     * Creates an {@link ItemStack} of this item with a count of 1.
     *
     * @return The created item stack
     * @throws IllegalStateException If the created stack is unexpectedly empty
     */
    public ItemStack toStack() {
        return this.toStack(1);
    }

    /**
     * Creates an {@link ItemStack} of this item with the specified count.
     *
     * @param count The stack size
     * @return The created item stack
     * @throws IllegalStateException If the created stack is unexpectedly empty
     */
    public ItemStack toStack(int count) {
        ItemStack stack = this.asItem().getDefaultInstance();
        if (stack.isEmpty())
            throw new IllegalStateException("Obtained empty item stack; incorrect getDefaultInstance() call?");
        stack.setCount(count);
        return stack;
    }

    /**
     * Creates a new item registry holder using a {@link ResourceLocation}.
     *
     * @param <T>      The concrete item type
     * @param key      The resource location identifying the item
     * @param supplier Supplier providing the item instance
     * @return The created registry holder
     */
    public static <T extends Item> RegistryItem<T> createItem(ResourceLocation key, Supplier<T> supplier) {
        return createItem(ResourceKey.create(Registries.ITEM, key), supplier);
    }

    /**
     * Creates a new item registry holder using a {@link ResourceKey}.
     *
     * @param <T>      The concrete item type
     * @param key      The resource key identifying the item
     * @param supplier Supplier providing the item instance
     * @return The created registry holder
     */
    public static <T extends Item> RegistryItem<T> createItem(ResourceKey<Item> key, Supplier<T> supplier) {
        return new RegistryItem<T>(key, supplier);
    }

    /**
     * Constructs a new item registry holder.
     *
     * @param key      The resource key identifying the item
     * @param supplier Supplier providing the item instance
     */
    protected RegistryItem(ResourceKey<Item> key, Supplier<T> supplier) {
        super(key, supplier);
    }

    /**
     * Gets this item instance.
     *
     * @return The item itself
     */
    @Override
    public Item asItem() {
        return get();
    }

    /**
     * Binds this holder to a concrete registry holder.
     *
     * @param holder The concrete holder from the registry
     */
    @Override
    public void bind(Holder<Item> holder) {
        super.bind(holder);
    }
}
