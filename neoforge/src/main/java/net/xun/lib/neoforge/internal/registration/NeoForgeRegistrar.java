package net.xun.lib.neoforge.internal.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registration.BlockEntry;
import net.xun.lib.common.api.registration.ItemEntry;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.api.registration.RegistryEntry;
import net.xun.lib.neoforge.XunLibNeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@ApiStatus.Internal
public class NeoForgeRegistrar<T> implements Registrar<T> {
    private final DeferredRegister<T> deferredRegister;
    private final String namespace;

    private final List<RegistryEntry<T, ? extends T>> entries = new ArrayList<>();

    private boolean registered;

    public NeoForgeRegistrar(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.deferredRegister = DeferredRegister.create(Objects.requireNonNull(registryKey, "registryKey"), namespace);
        this.namespace = Objects.requireNonNull(namespace, "namespace");
    }

    @Override
    public synchronized <R extends T> RegistryEntry<T, R> register(String name, Supplier<? extends R> supplier) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(supplier, "supplier");

        DeferredHolder<T, R> holder = deferredRegister.register(name, supplier);

        RegistryEntry<T, R> entry = createEntry(holder.getKey(), () -> Optional.of(holder));

        entry.bind(holder);
        entries.add(entry);

        return entry;
    }

    @Override
    public synchronized void registerEntries() {
        if (registered) {
            return;
        }

        deferredRegister.register(XunLibNeoForge.getEventBusOf(namespace));
        registered = true;
    }

    @Override
    public synchronized @NotNull Iterator<RegistryEntry<T, ? extends T>> iterator() {
        return List.copyOf(entries).iterator();
    }

    public static final class Items extends NeoForgeRegistrar<Item> implements Registrar.Items {
        public Items(String namespace) {
            super(Registries.ITEM, namespace);
        }

        @Override
        public <I extends Item> ItemEntry<I> registerItem(String name, Supplier<I> supplier) {
            return (ItemEntry<I>) register(name, supplier);
        }

        @Override
        public ItemEntry<Item> registerSimple(String name, Item.Properties properties) {
            return registerItem(name, () -> new Item(properties));
        }
    }

    public static final class Blocks extends NeoForgeRegistrar<Block> implements Registrar.Blocks {
        private final Registrar.Items itemRegistrar;

        public Blocks(String namespace, Registrar.Items itemRegistrar) {
            super(Registries.BLOCK, namespace);
            this.itemRegistrar = Objects.requireNonNull(itemRegistrar, "itemRegistrar");
        }

        @Override
        public <B extends Block> BlockEntry<B> registerBlock(String name, Supplier<? extends B> supplier) {
            return (BlockEntry<B>) register(name, supplier);
        }

        @Override
        public <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier<? extends B> blockSupplier) {
            BlockEntry<B> block = registerBlock(name, blockSupplier);

            itemRegistrar.register(name, () -> new BlockItem(block.get(), new Item.Properties()));

            return block;
        }

        @Override
        public <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier<? extends B> supplier, Supplier<? extends BlockItem> itemSupplier) {
            BlockEntry<B> block = registerBlock(name, supplier);
            itemRegistrar.register(name, itemSupplier);

            return block;
        }
    }
}
