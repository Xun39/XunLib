package net.xun.lib.neoforge.api.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.*;
import net.xun.lib.common.api.registration.Register;
import net.xun.lib.common.api.registration.RegistryHolder;

public class NeoForgeRegister<T> extends Register<T> {
    private final DeferredRegister<T> deferredRegister;
    private boolean attached;

    public NeoForgeRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        super(registry, namespace);
        this.deferredRegister = DeferredRegister.create(registry, namespace);
    }

    private IEventBus getModEventBus() {
        return ModList.get()
                .getModContainerById(getNamespace())
                .map(ModContainer::getEventBus)
                .orElseThrow(() -> new IllegalStateException("Could not find mod event bus for mod: " + getNamespace()));
    }

    @Override
    public void register() {
        if (!attached) {
            deferredRegister.register(getModEventBus());
            attached = true;
        }

        super.register();
    }

    @Override
    protected <I extends T> void bind(RegistryHolder<T, I> holder) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        DeferredHolder<T, I> deferredHolder = deferredRegister.register(id.getPath(), holder.getSupplier());
        holder.bind(deferredHolder);
    }

    public static class Blocks extends Register.Blocks {
        private final DeferredRegister.Blocks deferredRegister;
        private boolean attached;

        public Blocks(String namespace) {
            super(namespace);
            this.deferredRegister = DeferredRegister.createBlocks(namespace);
        }

        private IEventBus getModEventBus() {
            return ModList.get()
                    .getModContainerById(getNamespace())
                    .map(ModContainer::getEventBus)
                    .orElseThrow(() -> new IllegalStateException("Could not find mod event bus for mod: " + getNamespace()));
        }

        @Override
        public void register() {
            if (!attached) {
                deferredRegister.register(getModEventBus());
                attached = true;
            }

            super.register();
        }

        @Override
        protected <B extends Block> void bind(RegistryHolder<Block, B> holder) {
            ResourceLocation id = holder.unwrapKey().orElseThrow().location();
            DeferredHolder<Block, B> deferredHolder = deferredRegister.register(id.getPath(), holder.getSupplier());
            holder.bind(deferredHolder);
        }
    }

    public static class Items extends Register.Items {
        private final DeferredRegister.Items deferredRegister;
        private boolean attached;

        public Items(String namespace) {
            super(namespace);
            this.deferredRegister = DeferredRegister.createItems(namespace);
        }

        private IEventBus getModEventBus() {
            return ModList.get()
                    .getModContainerById(getNamespace())
                    .map(ModContainer::getEventBus)
                    .orElseThrow(() -> new IllegalStateException("Could not find mod event bus for mod: " + getNamespace()));
        }

        @Override
        public void register() {
            if (!attached) {
                deferredRegister.register(getModEventBus());
                attached = true;
            }

            super.register();
        }

        @Override
        protected <I extends Item> void bind(RegistryHolder<Item, I> holder) {
            ResourceLocation id = holder.unwrapKey().orElseThrow().location();
            DeferredHolder<Item, I> deferredHolder = deferredRegister.register(id.getPath(), holder.getSupplier());
            holder.bind(deferredHolder);
        }
    }
}
