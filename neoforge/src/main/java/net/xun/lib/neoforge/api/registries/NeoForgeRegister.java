package net.xun.lib.neoforge.api.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.*;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryBlock;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.common.api.registries.RegistryItem;
import net.xun.lib.neoforge.internal.XunLibNeoForge;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class NeoForgeRegister<T> extends Register<T> {

    private final DeferredRegister<T> deferredRegister;
    private final Map<ResourceLocation, RegistryHolder<T, ?>> pendingBindings = new HashMap<>();
    private boolean eventListenerRegistered = false;

    public NeoForgeRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        super(registry, namespace);
        this.deferredRegister = DeferredRegister.create(registry, namespace);
    }

    @Override
    public void register() {
        if (eventListenerRegistered) return;

        IEventBus modEventBus = XunLibNeoForge.getModEventBus();
        deferredRegister.register(modEventBus);
        modEventBus.addListener(this::handleRegistryEvent);

        eventListenerRegistered = true;
        super.register();
    }

    private void handleRegistryEvent(RegisterEvent event) {
        if (!event.getRegistryKey().equals(this.getRegistryKey())) {
            return;
        }

        Registry<T> registry = event.getRegistry(this.getRegistryKey());
        for (Map.Entry<ResourceLocation, RegistryHolder<T, ?>> entry : pendingBindings.entrySet()) {
            ResourceLocation id = entry.getKey();
            RegistryHolder<T, ?> holder = entry.getValue();

            ResourceKey<T> key = ResourceKey.create(this.getRegistryKey(), id);

            Holder<T> holderRef = registry.getHolder(key).orElseThrow(
                    () -> new IllegalStateException("Object not registered: " + id)
            );

            holder.bind(holderRef);
        }
        pendingBindings.clear();
    }

    @Override
    protected <I extends T> void bind(RegistryHolder<T, I> holder) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        Supplier<I> supplier = holder.getSupplier();

        deferredRegister.register(id.getPath(), supplier);

        pendingBindings.put(id, holder);
    }

    public static class Blocks extends Register.Blocks {
        private final NeoForgeRegister<Block> neoForgeRegister;

        public Blocks(String namespace) {
            super(namespace);
            this.neoForgeRegister = new NeoForgeRegister<>(Registries.BLOCK, namespace);
        }

        @Override
        public <B extends Block> RegistryBlock<B> register(String name, Supplier<B> supplier) {
            RegistryBlock<B> holder = super.register(name, supplier);

            neoForgeRegister.bind(holder);
            return holder;
        }

        @Override
        public void register() {
            neoForgeRegister.register();
        }
    }

    public static class Items extends Register.Items {
        private final NeoForgeRegister<Item> neoForgeRegister;

        public Items(String namespace) {
            super(namespace);
            this.neoForgeRegister = new NeoForgeRegister<>(Registries.ITEM, namespace);
        }

        @Override
        public <I extends Item> RegistryItem<I> register(String name, Supplier<I> supplier) {
            RegistryItem<I> holder = super.register(name, supplier);

            neoForgeRegister.bind(holder);
            return holder;
        }

        @Override
        public void register() {
            neoForgeRegister.register();
        }
    }
}
