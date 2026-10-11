package net.xun.lib.common.api.registration;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Optional;
import java.util.function.Supplier;

public interface Registrar<T> extends Iterable<RegistryEntry<T, ? extends T>> {

    <R extends T> RegistryEntry<T, R> register(String name, Supplier<? extends R> supplier);

    void registerEntries();

    default <R extends T> RegistryEntry<T, R> createEntry(ResourceKey<T> key, Supplier<Optional<Holder<T>>> holderLookup) {
        return RegistryEntry.create(key, holderLookup);
    }

    interface Items extends Registrar<Item> {
        <I extends Item> ItemEntry<I> registerItem(String name, Supplier<I> supplier);

        ItemEntry<Item> registerSimple(String name, Item.Properties properties);

        @Override
        default <R extends Item> RegistryEntry<Item, R> createEntry(ResourceKey<Item> key, Supplier<Optional<Holder<Item>>> holderLookup) {
            return ItemEntry.createItem(key, holderLookup);
        }
    }

    interface Blocks extends Registrar<Block> {
        <B extends Block> BlockEntry<B> registerBlock(String name, Supplier<? extends B> supplier);

        <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier<? extends B> supplier);

        <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier< ? extends B> supplier, Supplier<? extends BlockItem> itemSupplier);

        @Override
        default <R extends Block> RegistryEntry<Block, R> createEntry(ResourceKey<Block> key, Supplier<Optional<Holder<Block>>> holderLookup) {
            return BlockEntry.createBlock(key, holderLookup);
        }
    }
}
