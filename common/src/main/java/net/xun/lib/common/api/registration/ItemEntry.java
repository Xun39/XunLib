package net.xun.lib.common.api.registration;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ItemEntry<I extends Item> extends RegistryEntry<Item, I> implements ItemLike {
    protected ItemEntry(ResourceKey<Item> key, Supplier<Optional<Holder<Item>>> holderLookup) {
        super(key, holderLookup);
    }

    public static <I extends Item> ItemEntry<I> createItem(ResourceKey<Item> key, Supplier<Optional<Holder<Item>>> holderLookup) {
        return new ItemEntry<>(key, holderLookup);
    }

    @Override
    public @NotNull Item asItem() {
        return get();
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
