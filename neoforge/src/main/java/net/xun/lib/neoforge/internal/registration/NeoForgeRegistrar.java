package net.xun.lib.neoforge.internal.registration;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registration.BlockEntry;
import net.xun.lib.common.api.registration.ItemEntry;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.api.registration.RegistryEntry;
import net.xun.lib.common.internal.registration.RegistrationState;
import net.xun.lib.neoforge.XunLibNeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.Internal
public class NeoForgeRegistrar<T> implements Registrar<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final DeferredRegister<T> deferredRegister;
    private final String namespace;

    private final Set<ResourceLocation> ids = new LinkedHashSet<>();
    private final List<RegistryEntry<T, ? extends T>> entries = new ArrayList<>();

    private RegistrationState state = RegistrationState.STARTED;

    public NeoForgeRegistrar(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.namespace = Objects.requireNonNull(namespace, "namespace");
        this.registryKey = Objects.requireNonNull(registryKey, "registryKey");
        this.deferredRegister = DeferredRegister.create(registryKey, namespace);
    }

    @Override
    public synchronized <R extends T> RegistryEntry<T, R> register(String name, Supplier<? extends R> supplier) {
        if (state != RegistrationState.STARTED) {
            throw new IllegalStateException("Cannot register entries in registry " + registryKey.location() + " after registration has started (state: " + state + ")");
        }
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(supplier, "supplier");

        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
        if (!ids.add(id)) {
            throw new IllegalArgumentException("Duplicate registration '" + id + "' in registry " + registryKey.location());
        } // even though DeferredRegister is already handling that by default, we are still going to do that explicitly

        DeferredHolder<T, R> holder = deferredRegister.register(name, supplier);

        RegistryEntry<T, R> entry = createEntry(holder.getKey(), () -> Optional.of(holder));
        entry.bind(holder);
        entries.add(entry);

        return entry;
    }

    @Override
    public synchronized void registerEntries() {
        if (state == RegistrationState.REGISTERED) {
            return;
        }
        if (state == RegistrationState.REGISTERING) {
            throw new IllegalStateException("Registration is already in progress for registry " + registryKey.location());
        }

        state = RegistrationState.REGISTERING;
        try {
            deferredRegister.register(XunLibNeoForge.getEventBusOf(namespace));
            state = RegistrationState.REGISTERED;
        }
        catch (RuntimeException e) {
            throw new RuntimeException("Registration failed for unknown reason");
        }
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
        @SuppressWarnings("unchecked")
        public synchronized <I extends Item> ItemEntry<I> register(String name, Supplier<? extends I> supplier) {
            return (ItemEntry<I>) super.register(name, supplier);
        }
    }

    public static final class Blocks extends NeoForgeRegistrar<Block> implements Registrar.Blocks {
        private final Registrar.Items itemRegistrar;

        public Blocks(String namespace, Registrar.Items itemRegistrar) {
            super(Registries.BLOCK, namespace);
            this.itemRegistrar = Objects.requireNonNull(itemRegistrar, "itemRegistrar");
        }

        @Override
        @SuppressWarnings("unchecked")
        public synchronized  <B extends Block> BlockEntry<B> register(String name, Supplier<? extends B> supplier) {
            return (BlockEntry<B>) super.register(name, supplier);
        }

        @Override
        public <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier<? extends B> supplier, Supplier<? extends BlockItem> itemSupplier) {
            BlockEntry<B> block = this.register(name, supplier);
            itemRegistrar.register(name, itemSupplier);
            return block;
        }

        @Override
        public <B extends Block> BlockEntry<B> registerWithItem(String name, Supplier<? extends B> blockSupplier, Function<? super B, ? extends BlockItem> itemFactory) {
            BlockEntry<B> block = this.register(name, blockSupplier);
            itemRegistrar.register(name, () -> itemFactory.apply(block.get()));
            return block;
        }
    }
}
