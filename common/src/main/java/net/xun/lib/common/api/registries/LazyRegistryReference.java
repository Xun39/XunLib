package net.xun.lib.common.api.registries;

import java.util.Objects;
import java.util.function.Supplier;

public class LazyRegistryReference<T> implements Supplier<T> {

    private final String name;
    private final Supplier<T> supplier;
    private T value;

    public LazyRegistryReference(String name, Supplier<T> supplier) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.supplier = Objects.requireNonNull(supplier, "Supplier cannot be null");
    }

    @Override
    public T get() {
        T result = value;
        if (result == null) {
            synchronized (this) {
                result = value;
                if (result == null) {
                    value = result = supplier.get();
                }
            }
        }
        return result;
    }

    public String getName() {
        return name;
    }
}