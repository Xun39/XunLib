package net.xun.lib.fabric.internal.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.api.registration.BlockEntry;
import net.xun.lib.common.api.registration.ItemEntry;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.api.registration.RegistryEntry;
import net.xun.lib.common.api.util.RegistryUtil;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Supplier;

@ApiStatus.Internal
public class FabricRegistrar<T> implements Registrar<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String namespace;

    private final Map<String, PendingEntry<T, ? extends T>> pendingRegistrations = new LinkedHashMap<>();

    private volatile Registry<T> registry;
    private boolean registered;

    public FabricRegistrar(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.registryKey = Objects.requireNonNull(registryKey, "registryKey");
        this.namespace = Objects.requireNonNull(namespace, "namespace");
    }

    @Override
    public synchronized <R extends T> RegistryEntry<T, R> register(String name, Supplier<? extends R> supplier) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(supplier, "supplier");

        if (registered) {
            throw new IllegalStateException("Cannot register entries after registration has started: " + registryKey.location());
        }

        if (pendingRegistrations.containsKey(name)) {
            throw new IllegalArgumentException("Duplicate registration '" + namespace + ":" + name + "' in registry " + registryKey.location());
        }

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
        ResourceKey<T> key = ResourceKey.create(registryKey, id);
        RegistryEntry<T, R> entry = createEntry(key, () -> RegistryUtil.findHolderFromRegistry(key, registry));

        pendingRegistrations.put(name, new PendingEntry<>(key, supplier, entry));

        return entry;
    }

    @Override
    public synchronized void registerEntries() {
        if (registered) {
            return;
        }

        if (pendingRegistrations.isEmpty()) {
            registered = true;
            return;
        }

        Registry<T> registry = RegistryUtil.getRegistryFromRegistryKey(registryKey);
        this.registry = registry;

        // Prevent registration from being attempted twice if a failure occurs partway through the registration process.
        registered = true;

        for (PendingEntry<T, ? extends T> pending : pendingRegistrations.values()) {
            pending.register(registry);
        }
    }

    @Override
    public synchronized @NotNull Iterator<RegistryEntry<T, ? extends T>> iterator() {
        List<RegistryEntry<T, ? extends T>> snapshot = new ArrayList<>(pendingRegistrations.size());

        for (PendingEntry<T, ? extends T> pending : pendingRegistrations.values()) {
            snapshot.add(pending.entry());
        }

        return snapshot.iterator();
    }

    public static final class Items extends FabricRegistrar<Item> implements Registrar.Items {
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

    public static final class Blocks extends FabricRegistrar<Block> implements Registrar.Blocks {
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
