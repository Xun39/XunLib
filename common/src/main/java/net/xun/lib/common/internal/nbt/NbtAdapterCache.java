package net.xun.lib.common.internal.nbt;

import net.xun.lib.common.api.nbt.INbtAdapter;
import net.xun.lib.common.api.nbt.adapters.GenericNbtAdapter;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry cache for NBT serialization adapters with thread-safe initialization.
 * <p>
 * Manages a global registry of {@link INbtAdapter} implementations discovered through:
 * <ol>
 *   <li>ServiceLoader API (automatic discovery)</li>
 *   <li>Manual registration (not shown)</li>
 * </ol>
 *
 * <p><b>Initialization Characteristics:</b>
 * <ul>
 *   <li>Lazy initialization upon first access</li>
 *   <li>Double-checked locking for thread safety</li>
 *   <li>ConcurrentHashMap backing store (safe for concurrent reads)</li>
 * </ul>
 *
 * <p><b>Lookup Logic:</b>
 * <pre>
 * 1. Exact type match in registered adapters
 * 2. {@link GenericNbtAdapter} fallback
 * </pre>
 *
 * @see INbtAdapter#getAdapterFor(Class) Public access API
 */
@ApiStatus.Internal
public class NbtAdapterCache {

    /**
     * Concurrent registry map storing adapters by target type
     */
    public static final Map<Class<?>, INbtAdapter<?>> ADAPTERS = new ConcurrentHashMap<>();

    /**
     * Initialization state flag (volatile for double-checked locking)
     */
    public static volatile boolean initialized = false;

    /**
     * Retrieves the appropriate adapter for a given type with fallback handling.
     *
     * @param type The target serialization type (non-null)
     * @return Registered adapter or GenericNbtAdapter if none found
     * @throws IllegalStateException If initialization fails due to duplicate adapters
     */
    public static INbtAdapter<?> findAdapter(Class<?> type) {
        if (!initialized) {
            initialize();
        }
        return ADAPTERS.getOrDefault(type, new GenericNbtAdapter());
    }

    /**
     * Initializes the adapter registry using ServiceLoader discovery.
     * <p>
     * Synchronized to prevent concurrent initialization with:
     * - Volatile write for visibility
     * - ConcurrentHashMap for thread-safe population
     *
     * @throws IllegalStateException For duplicate adapter registrations
     */
    public static synchronized void initialize() {
        if (initialized) return;

        ServiceLoader<INbtAdapter> loader = ServiceLoader.load(INbtAdapter.class);
        for (INbtAdapter<?> adapter : loader) {
            Class<?> targetType = adapter.getTargetType();
            if (ADAPTERS.containsKey(targetType)) {
                throw new IllegalStateException("Duplicate adapter for: " + targetType.getName());
            }
            ADAPTERS.put(targetType, adapter);
        }
        initialized = true;
    }
}
