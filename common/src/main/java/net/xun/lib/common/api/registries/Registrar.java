package net.xun.lib.common.api.registries;

/*
    This is a class from the Artifacts mod

    MIT License

    Copyright (c) 2019-2021

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files (the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions:

    The above copyright notice and this permission notice shall be included in all
    copies or substantial portions of the Software.
*/

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.internal.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

public abstract class Registrar<T> implements Iterable<T> {

    private final ResourceKey<Registry<T>> registry;
    private final List<RegistryHolder<T, ?>> entries = new ArrayList<>();

    public Registrar(ResourceKey<Registry<T>> registry) {
        this.registry = registry;
    }

    public static <R> Registrar<R> create(ResourceKey<Registry<R>> registry, String namespace) {
        return Services.PLATFORM.createRegistrar(registry, namespace);
    }

    public ResourceKey<Registry<T>> getRegistry() {
        return registry;
    }

    public Collection<RegistryHolder<T, ?>> getEntries() {
        return entries;
    }

    public <R extends T> RegistryHolder<T, R> register(String name, Supplier<R> supplier) {
        RegistryHolder<T, R> holder = new RegistryHolder<>(CommonUtils.createKey(registry, name), supplier);
        entries.add(holder);

        if (getRegistry().equals(Registries.ATTRIBUTE)
                || getRegistry().equals(Registries.MOB_EFFECT)
                || getRegistry().equals(Registries.DATA_COMPONENT_TYPE)
        ) {
            bind(holder);
        }

        return holder;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return entries.stream().map(RegistryHolder::value).iterator();
    }

    public void register() {
        if (!getRegistry().equals(Registries.ATTRIBUTE)
                && !getRegistry().equals(Registries.MOB_EFFECT)
                && !getRegistry().equals(Registries.DATA_COMPONENT_TYPE)
        ) {
            for (RegistryHolder<T, ?> holder : getEntries()) {
                bind(holder);
            }
        }
    }

    protected abstract <R extends T> void bind(RegistryHolder<T, R> holder);
}
