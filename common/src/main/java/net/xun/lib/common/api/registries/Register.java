package net.xun.lib.common.api.registries;

/*
    This class is from the Artifacts mod

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
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.internal.platform.Services;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract base class for registry management that simplifies registration of game objects.
 * <p>
 * This class provides a convenient way to register objects to Minecraft registries with automatic
 * holder binding. It supports iteration over registered objects and handles both block and item
 * registration through specialized inner classes.
 * </p>
 *
 * @param <T> The base type of objects being registered (e.g. Block, Item)
 */
public abstract class Register<T> implements Iterable<T> {

    /**
     * Creates a register for the specified registry and namespace.
     *
     * @param <R>       The registry object type
     * @param registry  The target registry
     * @param namespace The namespace for registered objects
     * @return A new register instance
     */
    public static <R> Register<R> create(Registry<R> registry, String namespace) {
        return Services.PLATFORM.createRegistrar(registry.key(), namespace);
    }

    /**
     * Creates a register for the specified registry key and namespace.
     *
     * @param <R>       The registry object type
     * @param registry  The resource key of the target registry
     * @param namespace The namespace for registered objects
     * @return A new register instance
     */
    public static <R> Register<R> create(ResourceKey<Registry<R>> registry, String namespace) {
        return Services.PLATFORM.createRegistrar(registry, namespace);
    }

    /**
     * Creates a specialized item register for the given namespace.
     *
     * @param namespace The namespace for registered items
     * @return A new item register
     */
    public static Items createItems(String namespace) {
        return new Items(namespace);
    }

    /**
     * Creates a specialized block register for the given namespace.
     *
     * @param namespace The namespace for registered blocks
     * @return A new block register
     */
    public static Blocks createBlocks(String namespace) {
        return new Blocks(namespace);
    }

    /** The resource key of the target registry */
    private final ResourceKey<? extends Registry<T>> registryKey;

    /** The namespace for registered objects */
    private final String namespace;

    /** List of registry holders for objects to be registered */
    private final List<RegistryHolder<T, ?>> entries = new ArrayList<>();

    /**
     * Constructs a new register instance.
     *
     * @param registry  The resource key of the target registry
     * @param namespace The namespace for registered objects
     */
    protected Register(ResourceKey<? extends Registry<T>> registry, String namespace) {
        this.registryKey = registry;
        this.namespace = namespace;
    }

    /**
     * Gets the resource key of the target registry.
     *
     * @return The registry resource key
     */
    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return this.registryKey;
    }

    /**
     * Gets all registry holder entries.
     *
     * @return Collection of registry holders
     */
    public Collection<RegistryHolder<T, ?>> getEntries() {
        return this.entries;
    }

    /**
     * Gets the namespace for registered objects.
     *
     * @return The namespace
     */
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Registers a new object to this registry.
     * <p>
     * For certain registries (attributes, mob effects, data component types),
     * binding happens immediately during registration.
     *
     * @param <R>      The concrete type of the object to register
     * @param name     The name of the object (without namespace)
     * @param supplier Supplier providing the object instance
     * @return The created registry holder
     */
    public <R extends T> RegistryHolder<T, R> register(String name, Supplier<R> supplier) {
        RegistryHolder<T, R> holder = new RegistryHolder<>(CommonUtils.createKey(registryKey, name), supplier);
        entries.add(holder);

        // Immediately bind for specific registry types
        if (getRegistryKey().equals(Registries.ATTRIBUTE)
                || getRegistryKey().equals(Registries.MOB_EFFECT)
                || getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)
        ) {
            bind(holder);
        }

        return holder;
    }

    /**
     * {@inheritDoc}
     * @return Iterator over registered values
     */
    @NotNull
    @Override
    public Iterator<T> iterator() {
        return entries.stream().map(RegistryHolder::value).iterator();
    }

    /**
     * Performs final registration by binding all holders to the actual registry.
     * <p>
     * This should be called during mod initialization after the registry is available.
     * Note: Certain registry types are bound immediately and skip this step.
     */
    public void register() {
        // Bind all entries except those already bound during registration
        if (!getRegistryKey().equals(Registries.ATTRIBUTE)
                && !getRegistryKey().equals(Registries.MOB_EFFECT)
                && !getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)
        ) {
            for (RegistryHolder<T, ?> holder : getEntries()) {
                bind(holder);
            }
        }
    }

    /**
     * Binds a registry holder to the actual registry entry.
     *
     * @param <R>    The concrete type of the registered object
     * @param holder The registry holder to bind
     */
    protected abstract <R extends T> void bind(RegistryHolder<T, R> holder);

    /**
     * Specialized register implementation for blocks.
     * <p>
     * Provides block-specific registration methods and automatic binding.
     */
    public static class Blocks extends Register<Block> {
        /**
         * Constructs a block register.
         *
         * @param namespace The namespace for registered blocks
         */
        protected Blocks(String namespace) {
            super(Registries.BLOCK, namespace);
        }

        /**
         * Registers a new block.
         *
         * @param <B>      The concrete block type
         * @param name     The name of the block (without namespace)
         * @param supplier Supplier providing the block instance
         * @return A specialized block registry holder
         */
        public <B extends Block> RegistryBlock<B> register(String name, Supplier<B> supplier) {
            RegistryHolder<Block, B> holder = super.register(name, supplier);
            return new RegistryBlock<>(holder.key, supplier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <R extends Block> void bind(RegistryHolder<Block, R> holder) {
            Services.PLATFORM.bindBlock(holder, getNamespace());
        }
    }

    /**
     * Specialized register implementation for items.
     * <p>
     * Provides item-specific registration methods and automatic binding.
     */
    public static class Items extends Register<Item> {
        /**
         * Constructs an item register.
         *
         * @param namespace The namespace for registered items
         */
        protected Items(String namespace) {
            super(Registries.ITEM, namespace);
        }

        /**
         * Registers a new item.
         *
         * @param <I>      The concrete item type
         * @param name     The name of the item (without namespace)
         * @param supplier Supplier providing the item instance
         * @return A specialized item registry holder
         */
        public <I extends Item> RegistryItem<I> register(String name, Supplier<I> supplier) {
            RegistryHolder<Item, I> holder = super.register(name, supplier);
            return new RegistryItem<>(holder.key, supplier);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected <R extends Item> void bind(RegistryHolder<Item, R> holder) {
            Services.PLATFORM.bindItem(holder, getNamespace());
        }
    }
}