package net.xun.lib.common.api.util;

import java.util.function.Supplier;

public class LazyReference<T> implements Supplier<T> {
    private final String name;
    private final Supplier<T> supplier;
    private T value;

    public LazyReference(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (value == null) {
            value = supplier.get();
        }
        return value;
    }

    public String getName() {
        return name;
    }
}
