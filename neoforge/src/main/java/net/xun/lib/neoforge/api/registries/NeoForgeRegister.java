package net.xun.lib.neoforge.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registries.Register;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.neoforge.internal.XunLibNeoForge;

import java.util.function.Supplier;

public class NeoForgeRegister<T> extends Register<T> {

    private final DeferredRegister<T> deferredRegister;

    public NeoForgeRegister(ResourceKey<? extends Registry<T>> registry, String namespace) {
        super(registry, namespace);
        this.deferredRegister = DeferredRegister.create(registry, namespace);
    }

    @Override
    public void register() {
        super.register();
        XunLibNeoForge.addDeferredRegister(deferredRegister);
    }

    @Override
    protected <R extends T> void bind(RegistryHolder<T, R> holder) {
        ResourceLocation id = holder.unwrapKey().orElseThrow().location();
        Supplier<R> supplier = holder.getSupplier();

        DeferredHolder<T, R> deferredHolder = deferredRegister.register(id.getPath(), supplier);

        holder.bind(deferredHolder);
    }
}
