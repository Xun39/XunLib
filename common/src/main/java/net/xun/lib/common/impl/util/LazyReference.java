package net.xun.lib.common.impl.util;

import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Internal utility class for a late‑bound reference to a {@link Supplier}.
 * <p>
 * <strong>Note:</strong> This class is part of the internal API and is not intended for use by external mods.
 * It may change or be removed without notice in future versions.
 * </p>
 * <p>
 * A {@code LazyReference} holds a name and a volatile reference to a {@code Supplier<T>}.
 * The supplier can be set once via {@link #bind(Supplier)}. Every call to {@link #get()}
 * invokes the supplier's {@link Supplier#get()} method.
 * </p>
 * <p>
 * This pattern is useful for breaking circular dependencies during initialization:
 * an object can be declared early and its actual provider bound later, after the
 * provider itself has been fully constructed.
 * </p>
 * <p>
 * <strong>Important:</strong> This class does <em>not</em> memoize the result of the supplier.
 * If a singleton behavior is desired, the bound supplier itself must implement caching
 * (e.g. by returning a pre‑initialized instance or using its own lazy initialization).
 * </p>
 * <p>
 * This is an internal implementation detail of the Armory API. Use the public API classes
 * ({@link net.xun.lib.common.api.item.tools.ToolSet}, {@link net.xun.lib.common.api.item.armor.ArmorSet})
 * instead of interacting with this class directly.
 * </p>
 *
 * @param <T> the type of object supplied by this reference
 * @since 1.0.0
 */
@ApiStatus.Internal
public class LazyReference<T> implements Supplier<T> {

    private final String name;
    private volatile Supplier<T> delegate;

    /**
     * Constructs a new {@code LazyReference} with a descriptive name.
     * <p>
     * The name is typically used for debugging, logging, or generating registry keys.
     * The supplier must be set later using {@link #bind(Supplier)} before any call to {@link #get()}.
     * </p>
     *
     * @param name a descriptive name for the reference (cannot be null)
     * @throws NullPointerException if {@code name} is null
     */
    public LazyReference(String name) {
        this.name = Objects.requireNonNull(name, "name");
    }

    /**
     * Returns the name associated with this lazy reference.
     *
     * @return the name (never null)
     */
    public String getName() {
        return name;
    }

    /**
     * Binds this reference to a concrete supplier.
     * <p>
     * After binding, every call to {@link #get()} will delegate to this supplier.
     * The supplier may be set only once; subsequent calls to this method are not
     * prevented but may lead to unpredictable behavior across threads.
     * </p>
     *
     * @param delegate the supplier that will provide the value (cannot be null)
     * @throws NullPointerException if {@code delegate} is null
     */
    public synchronized void bind(Supplier<T> delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    /**
     * Checks whether this reference has been bound to a supplier.
     *
     * @return {@code true} if {@link #bind(Supplier)} has been called at least once,
     *         {@code false} otherwise
     */
    public boolean isBound() {
        return delegate != null;
    }

    /**
     * Returns the value obtained from the bound supplier.
     * <p>
     * This method must only be called after {@link #bind(Supplier)} has been invoked.
     * Each call invokes the supplier's {@code get()} method – no result is cached by
     * this class. The method is thread‑safe in the sense that the volatile read of
     * the delegate is safe, but the supplier itself must be thread‑safe if called
     * concurrently.
     * </p>
     *
     * @return the value provided by the bound supplier
     * @throws IllegalStateException if this reference has not yet been bound to a supplier
     */
    @Override
    public T get() {
        Supplier<T> current = delegate;
        if (current == null) {
            throw new IllegalStateException("Item reference '" + name + "' has not been registered yet");
        }
        return current.get();
    }
}