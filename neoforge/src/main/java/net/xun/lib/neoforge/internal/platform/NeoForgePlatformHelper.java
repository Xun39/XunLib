package net.xun.lib.neoforge.internal.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.common.internal.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.xun.lib.neoforge.api.registries.NeoForgeRegister;
import net.xun.lib.neoforge.internal.XunLibNeoForge;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "NeoForge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public <T> Register<T> createRegistrar(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new NeoForgeRegister<>(registry, namespace);
    }

    @Override
    public <T extends Item> void bindItem(RegistryHolder<Item, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        String path = id.getPath();

        DeferredRegister.Items deferredRegister = XunLibNeoForge.getOrCreateItemDeferredRegister(namespace);

        DeferredItem<Item> deferredItem = deferredRegister.register(
                path,
                holder.getSupplier()
        );

        if (deferredItem.isBound()) {
            Holder<Item> holderRef = BuiltInRegistries.ITEM.getHolderOrThrow(
                    ResourceKey.create(Registries.ITEM, id)
            );

            holder.bind(holderRef);
        }
    }

    @Override
    public <T extends Block> void bindBlock(RegistryHolder<Block, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        String path = id.getPath();

        DeferredRegister.Blocks deferredRegister = XunLibNeoForge.getOrCreateBlockDeferredRegister(namespace);

        DeferredBlock<Block> deferredBlock = deferredRegister.register(
                path,
                holder.getSupplier()
        );

        if (deferredBlock.isBound()) {
            Holder<Block> holderRef = BuiltInRegistries.BLOCK.getHolderOrThrow(
                    ResourceKey.create(Registries.BLOCK, id)
            );

            holder.bind(holderRef);
        }
    }
}