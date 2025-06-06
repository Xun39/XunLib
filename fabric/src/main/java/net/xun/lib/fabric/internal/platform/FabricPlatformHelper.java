package net.xun.lib.fabric.internal.platform;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.internal.platform.services.IPlatformHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.xun.lib.fabric.api.registries.FabricRegister;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> Register<T> createRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        return new FabricRegister<>(registry, namespace);
    }

    @Override
    public Register.Blocks createBlockRegister(String namespace) {
        return new FabricRegister.FabricBlocks(namespace);
    }

    @Override
    public Register.Items createItemRegister(String namespace) {
        return new FabricRegister.FabricItems(namespace);
    }

    /* @Override
    public <T extends Item> void bindItem(RegistryHolder<Item, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        Item item = holder.getSupplier().get();

        Holder<Item> registeredHolder = Registry.registerForHolder(
                BuiltInRegistries.ITEM,
                id,
                item
        );

        holder.bind(registeredHolder);
    }

    @Override
    public <T extends Block> void bindBlock(RegistryHolder<Block, T> holder, String namespace) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        Block block = holder.getSupplier().get();

        Holder<Block> registeredHolder = Registry.registerForHolder(
                BuiltInRegistries.BLOCK,
                id,
                block
        );

        holder.bind(registeredHolder);
    } */
}
