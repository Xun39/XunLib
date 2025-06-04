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

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistryHolder<T, R extends T> implements Holder<T>, Supplier<R> {

    protected final ResourceKey<T> key;
    private final Supplier<R> supplier;
    private Holder<T> holder;

    protected RegistryHolder(ResourceKey<T> key, Supplier<R> supplier) {
        this.key = key;
        this.supplier = supplier;
    }

    public Supplier<R> getSupplier() {
        return supplier;
    }

    public void bind(Holder<T> holder) {
        if (this.holder != null) {
            throw new IllegalStateException();
        }
        this.holder = holder;
    }

    public Holder<T> holder() {
        return holder;
    }

    @Override
    @SuppressWarnings("unchecked")
    public R get() {
        return (R) value();
    }

    @Override
    public T value() {
        return holder.value();
    }

    @Override
    public boolean isBound() {
        return holder != null && holder.isBound();
    }

    @Override
    public boolean is(ResourceLocation resourceLocation) {
        return resourceLocation.equals(key.location());
    }

    @Override
    public boolean is(ResourceKey<T> resourceKey) {
        return resourceKey.equals(key);
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return predicate.test(key);
    }

    @Override
    public boolean is(TagKey<T> tagKey) {
        return isBound() && holder.is(tagKey);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean is(Holder<T> holder) {
        return isBound() && holder.is(holder);
    }

    @Override
    public Stream<TagKey<T>> tags() {
        return isBound() ? holder.tags() : Stream.empty();
    }

    @Override
    public Either<ResourceKey<T>, T> unwrap() {
        return Either.left(key);
    }

    @Override
    public Optional<ResourceKey<T>> unwrapKey() {
        return Optional.of(key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<T> holderOwner) {
        return isBound() && holder.canSerializeIn(holderOwner);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof Holder<?> h && h.kind() == Kind.REFERENCE && h.unwrapKey().isPresent() && h.unwrapKey().get() == this.key;
    }

    @Override
    public int hashCode() {
        return this.key.hashCode();
    }
}
