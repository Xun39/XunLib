package net.xun.lib.fabric.api.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.api.registration.Register;
import net.xun.lib.common.api.registration.RegistryHolder;

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
        Registry<R> registry = (Registry<R>) BuiltInRegistries.REGISTRY.get(key.location());
        if (registry == null) {
            throw new IllegalStateException("Could not find registry: " + key.location());
        }
        return registry;
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
        protected <B extends Block> void bind(RegistryHolder<Block, B> holder) {
            fabricRegister.bind(holder);
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
        protected <I extends Item> void bind(RegistryHolder<Item, I> holder) {
            fabricRegister.bind(holder);
        }
    }
}
