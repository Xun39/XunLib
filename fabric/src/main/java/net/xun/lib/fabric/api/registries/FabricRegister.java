package net.xun.lib.fabric.api.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryBlock;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.common.api.registries.RegistryItem;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FabricRegister<T> extends Register<T> {

    public FabricRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        super(registry, namespace);
    }

    @Override
    protected <R extends T> void bind(RegistryHolder<T, R> holder) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        R value = holder.getSupplier().get();

        Registry<T> registry = getRegistry(getRegistryKey());
        Registry.register(registry, id, value);

        Holder<T> registeredHolder = registry.getHolderOrThrow(ResourceKey.create(getRegistryKey(), id));

        holder.bind(registeredHolder);
    }

    @SuppressWarnings("unchecked")
    private static <R> Registry<R> getRegistry(ResourceKey<? extends Registry<R>> key) {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(key.location());
    }

    public static class FabricBlocks extends Blocks {
        private final FabricRegister<Block> fabricRegister;

        /**
         * Constructs a block register.
         *
         * @param namespace The namespace for registered blocks
         */
        public FabricBlocks(String namespace) {
            super(namespace);
            this.fabricRegister = new FabricRegister<>(Registries.BLOCK, namespace);
        }

        @Override
        public <B extends Block> RegistryBlock<B> register(String name, Supplier<B> supplier) {
            RegistryBlock<B> holder = super.register(name, supplier);

            fabricRegister.bind(holder);
            return holder;
        }

        @Override
        public void register() {
            fabricRegister.register();
        }
    }

    public static class FabricItems extends Items {
        private final FabricRegister<Item> fabricRegister;

        /**
         * Constructs an item register.
         *
         * @param namespace The namespace for registered blocks
         */
        public FabricItems(String namespace) {
            super(namespace);
            this.fabricRegister = new FabricRegister<>(Registries.ITEM, namespace);
        }

        @Override
        public <I extends Item> RegistryItem<I> register(String name, Supplier<I> supplier) {
            RegistryItem<I> holder = super.register(name, supplier);

            fabricRegister.bind(holder);
            return holder;
        }

        @Override
        public void register() {
            fabricRegister.register();
        }
    }
}
