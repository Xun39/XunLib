package net.xun.lib.fabric.api.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.common.api.util.CommonUtils;

public class FabricRegister<T> extends Register<T> {

    public FabricRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        super(registry, namespace);
    }

    @Override
    protected <R extends T> void bind(RegistryHolder<T, R> holder) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        R value = holder.getSupplier().get();

        Holder<T> registeredHolder = Registry.registerForHolder(
                getRegistry(getRegistryKey()),
                CommonUtils.createKey(
                        getRegistryKey(),
                        id.getPath()),
                value);

        holder.bind(registeredHolder);
    }

    @SuppressWarnings("unchecked")
    private static <R> Registry<R> getRegistry(ResourceKey<? extends Registry<R>> key) {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(key.location());
    }
}
