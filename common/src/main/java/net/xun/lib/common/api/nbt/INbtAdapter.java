package net.xun.lib.common.api.nbt;

import net.minecraft.nbt.Tag;
import net.xun.lib.common.api.nbt.adapters.GenericNbtAdapter;
import net.xun.lib.common.internal.misc.NbtUtils;
import net.xun.lib.common.internal.nbt.NbtAdapterCache;

import java.util.HashMap;
import java.util.Map;

/**
 * Defines a type-specific serialization contract for converting objects to/from NBT format.
 * <p>
 * Implementations provide custom persistence logic for complex types that cannot be automatically
 * handled by reflection-based serialization in {@link NbtUtils}. Registered adapters take precedence
 * over default serialization mechanisms, allowing fine-grained control over specific types.
 *
 * <p>The system falls back to {@link GenericNbtAdapter}which uses {@link NbtUtils}'s
 * reflection-based approach to handle common Java types.
 *
 * @param <T> The exact type this adapter serializes. Must match the registration type precisely.
 *
 * @see GenericNbtAdapter Default fallback implementation
 */
public interface INbtAdapter<T> {

    /**
     * Converts an object instance into its NBT representation.
     *
     * @param value Non-null instance to serialize. Implementations may assume valid non-null input
     *              when called through standard serialization pathways
     * @return Non-null NBT structure containing complete object state. Must be compatible with
     *         {@link #load(Tag)} for reconstruction
     * @throws IllegalArgumentException If the object contains unsupported data types or violates
     *                                  serialization constraints
     */
    Tag save(T value);

    /**
     * Reconstructs an object instance from its NBT representation.
     *
     * @param tag Non-null NBT data structure created by {@link #save(T)}. Must maintain the same
     *            structural format as originally serialized
     * @return Fully reconstructed object instance. May return null <strong>only</strong> if
     *         serialization explicitly preserved a null reference (not typical in standard use)
     * @throws ClassCastException If the tag structure doesn't match the expected format
     * @throws IllegalArgumentException If tag contains invalid or incompatible data
     */
    T load(Tag tag);

    /**
     * Returns the exact class this adapter is designed to handle.
     * <p>
     * Used by the adapter registry for type matching. The registry will use this adapter for all
     * serialization tasks where {@code Class.equals()} matches the target type exactly.
     */
    Class<T> getTargetType();

    /**
     * Retrieves the most appropriate adapter for a given type from the registry.
     * <p>
     * Performs automatic adapter discovery using the following priority:
     * <ol>
     *   <li>Exact match for registered adapter type</li>
     *   <li>{@link GenericNbtAdapter} as fallback for unregistered types</li>
     * </ol>
     *
     * @param type Target class needing serialization support
     * @return Non-null adapter instance capable of handling the specified type
     * @throws IllegalArgumentException If no adapter exists and {@link NbtUtils} cannot handle
     *                                  the type through the generic fallback
     */
    @SuppressWarnings("unchecked")
    static <T> INbtAdapter<T> getAdapterFor(Class<T> type) {
        return (INbtAdapter<T>) NbtAdapterCache.findAdapter(type);
    }
}
