package net.xun.lib.common.api.registration;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RegistryEntry<T, R extends T> implements Supplier<R>, Holder<T> {
    private final ResourceKey<T> key;
    private final Supplier<Optional<Holder<T>>> holderLookup;

    private volatile Supplier<? extends R> supplier;

    protected RegistryEntry(ResourceKey<T> key, Supplier<Optional<Holder<T>>> holderLookup) {
        this.key = Objects.requireNonNull(key, "key cannot be null");
        this.holderLookup = Objects.requireNonNull(holderLookup, "holderLookup cannot be null");
    }

    public static <T, R extends T> RegistryEntry<T, R> create(ResourceKey<T> key, Supplier<Optional<Holder<T>>> holderLookup) {
        return new RegistryEntry<>(key, holderLookup);
    }

    public synchronized void bind(Supplier<R> supplier) {
        this.supplier = Objects.requireNonNull(supplier, "supplier cannot be null");
    }

    private Optional<Holder<T>> findHolder() {
        return holderLookup.get();
    }

    private Holder<T> holder() {
        return findHolder().orElseThrow(() -> new IllegalStateException("Holder for entry '" + key + "' is not available yet"));
    }

    public Holder<T> getHolder() {
        return holder();
    }

    public ResourceLocation getId() {
        return key.location();
    }

    @Override
    public R get() {
        Supplier<? extends R> current = supplier;
        if (current == null) {
            throw new IllegalStateException("Entry '" + key + "' has not been initialized");
        }
        return current.get();
    }

    @Override
    public @NotNull T value() {
        return holder().value();
    }

    @Override
    public boolean isBound() {
        return findHolder().map(Holder::isBound).orElse(false);
    }

    @Override
    public boolean is(@NotNull ResourceLocation location) {
        return getId().equals(location);
    }

    @Override
    public boolean is(@NotNull ResourceKey<T> key) {
        return this.key.equals(key);
    }

    @Override
    public boolean is(Predicate<ResourceKey<T>> predicate) {
        return predicate.test(this.key);
    }

    @Override
    public boolean is(@NotNull TagKey<T> tagKey) {
        return holder().is(tagKey);
    }

    @Override
    @Deprecated
    public boolean is(@NotNull Holder<T> other) {
        return holder().is(other);
    }

    @Override
    public @NotNull Stream<TagKey<T>> tags() {
        return holder().tags();
    }

    @Override
    public @NotNull Either<ResourceKey<T>, T> unwrap() {
        return Either.left(key);
    }

    @Override
    public @NotNull Optional<ResourceKey<T>> unwrapKey() {
        return Optional.of(key);
    }

    @Override
    public Holder.@NotNull Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(@NotNull HolderOwner<T> owner) {
        return holder().canSerializeIn(owner);
    }
}
