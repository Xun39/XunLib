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

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A registry-backed holder implementation that wraps a registry entry and its supplier.
 * <p>
 * This class acts as a bridge between {@link Holder} and {@link Supplier}, allowing lazy initialization
 * and late binding to actual registry entries.
 * </p>
 *
 * @param <R> The base registry type (e.g. {@code Item})
 * @param <T> The concrete type of the held object (must extend {@code R})
 */
public class RegistryHolder<R, T extends R> implements Holder<R>, Supplier<T> {

    /** Resource key identifying the held object in the registry */
    protected final ResourceKey<R> key;

    /** Supplier for the concrete instance of the held object */
    private final Supplier<T> supplier;

    /** The actual holder instance after binding */
    private Holder<R> holder;

    /**
     * Creates a new RegistryHolder instance.
     *
     * @param key      Resource key identifying the object in the registry
     * @param supplier Supplier providing the concrete instance of the object
     */
    protected RegistryHolder(ResourceKey<R> key, Supplier<T> supplier) {
        this.key = key;
        this.supplier = supplier;
    }

    /**
     * Gets the underlying supplier for the held object.
     *
     * @return The supplier providing the concrete instance
     */
    public Supplier<T> getSupplier() {
        return supplier;
    }

    /**
     * Binds this holder to a concrete registry holder.
     * <p>
     * This should be called during registry initialization to link the holder
     * to the actual registry entry.
     *
     * @param holder The concrete holder from the registry
     * @throws IllegalStateException If already bound
     */
    public void bind(Holder<R> holder) {
        if (this.holder != null) {
            throw new IllegalStateException("Try to bind already existing value!");
        }

        this.holder = holder;
    }

    /**
     * Gets the underlying registry holder.
     *
     * @return The bound holder instance
     */
    public Holder<R> holder() {
        return holder;
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException If accessed before binding
     */
    @NotNull
    @Override
    public R value() {
        if (this.holder == null) {
            throw new NullPointerException("Trying to access unbound value: " + this.key);
        }

        return holder.value();
    }

    /**
     * {@inheritDoc}
     * @throws NullPointerException If accessed before binding
     */
    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) value();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isBound() {
        return holder != null && holder.isBound();
    }

    /** {@inheritDoc} */
    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return resourceLocation.equals(key.location());
    }

    /** {@inheritDoc} */
    @Override
    public boolean is(ResourceKey<R> resourceKey) {
        return resourceKey.equals(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean is(TagKey<R> tagKey) {
        return isBound() && holder.is(tagKey);
    }

    /** {@inheritDoc} */
    @Override
    @SuppressWarnings("deprecation")
    public boolean is(Holder<R> holder) {
        return isBound() && holder.is(holder);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<TagKey<R>> tags() {
        return isBound() ? holder.tags() : Stream.empty();
    }

    /** {@inheritDoc} */
    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(key);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(key);
    }

    /** {@inheritDoc} */
    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    /** {@inheritDoc} */
    @Override
    public boolean canSerializeIn(HolderOwner<R> holderOwner) {
        return isBound() && holder.canSerializeIn(holderOwner);
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Holder<?> h &&
                h.kind() == Kind.REFERENCE &&
                h.unwrapKey().isPresent() &&
                h.unwrapKey().get() == this.key;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}
