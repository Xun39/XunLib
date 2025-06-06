package net.xun.lib.neoforge.internal.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.internal.platform.services.IPlatformHelper;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.xun.lib.neoforge.api.registries.NeoForgeRegister;
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
    public <T> Register<T> createRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new NeoForgeRegister<>(registry, namespace);
    }

    @Override
    public Register.Blocks createBlockRegister(String namespace) {
        return new NeoForgeRegister.Blocks(namespace);
    }

    @Override
    public Register.Items createItemRegister(String namespace) {
        return new NeoForgeRegister.Items(namespace);
    }

    /* @Override
    public <T extends Item> void bindItem(RegistryHolder<Item, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();

        DeferredRegister.Items deferredRegister = XunLibNeoForge.getOrCreateItemDeferredRegister(namespace);

        if (!deferredRegister.getEntries().contains(id)) {
            DeferredItem<Item> deferredItem = deferredRegister.register(
                    id.getPath(),
                    holder.getSupplier()
            );

            if (deferredItem.isBound()) {
                Holder<Item> holderRef = BuiltInRegistries.ITEM.getHolderOrThrow(
                        ResourceKey.create(Registries.ITEM, id)
                );

                holder.bind(holderRef);
            }
        }
    }

    @Override
    public <T extends Block> void bindBlock(RegistryHolder<Block, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();

        DeferredRegister.Blocks deferredRegister = XunLibNeoForge.getOrCreateBlockDeferredRegister(namespace);

        if (!deferredRegister.getEntries().contains(id)) {
            DeferredBlock<Block> deferredBlock = deferredRegister.register(
                    id.getPath(),
                    holder.getSupplier()
            );

            if (deferredBlock.isBound()) {
                Holder<Block> holderRef = BuiltInRegistries.BLOCK.getHolderOrThrow(
                        ResourceKey.create(Registries.BLOCK, id)
                );

                holder.bind(holderRef);
            }
        }
    } */
}