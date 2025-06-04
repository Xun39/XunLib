package net.xun.lib.fabric.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.common.api.util.CommonUtils;

public class FabricRegistrar<T> extends Registrar<T> {

    public FabricRegistrar(ResourceKey<Registry<T>> registry) {
        super(registry);
    }

    @Override
    protected <R extends T> void bind(RegistryHolder<T, R> holder) {
        holder.bind(
                Registry.registerForHolder(
                        getRegistry(getRegistry()),
                        CommonUtils.createKey(
                                getRegistry(),
                                holder.unwrapKey().orElseThrow().location().getPath()),
                        holder.getSupplier().get())
        );
    }

    @SuppressWarnings("unchecked")
    private static <R> Registry<R> getRegistry(ResourceKey<Registry<R>> key) {
        return (Registry<R>) BuiltInRegistries.REGISTRY.get(key.location());
    }
}
