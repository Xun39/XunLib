package net.xun.lib.neoforge.api.registries;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.api.registries.RegistryHolder;
import net.xun.lib.neoforge.internal.XunLibNeoForge;

public class NeoForgeRegistrar<T> extends Registrar<T> {

    private final DeferredRegister<T> deferredRegister;

    public NeoForgeRegistrar(ResourceKey<Registry<T>> registry, String namespace) {
        super(registry);
        this.deferredRegister = DeferredRegister.create(registry, namespace);
    }

    @Override
    public void register() {
        super.register();
        XunLibNeoForge.addDeferredRegister(deferredRegister);
    }

    @Override
    protected <R extends T> void bind(RegistryHolder<T, R> holder) {
        holder.bind(
                deferredRegister.register(
                        holder.unwrapKey().orElseThrow().location().getPath(),
                        holder.getSupplier())
        );
    }
}
