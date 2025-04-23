package net.xun.lib.common.api.nbt.adapters;

import net.minecraft.nbt.Tag;
import net.xun.lib.common.api.nbt.INbtAdapter;
import net.xun.lib.common.internal.misc.NbtUtils;

/**
 * Default NBT adapter handling serialization through reflection-based {@link NbtUtils} methods.
 * <p>
 * Serves as the ultimate fallback in the adapter resolution chain, processing any type not
 * explicitly handled by a registered {@link INbtAdapter}. This implementation delegates to:
 * <ul>
 *   <li>{@link NbtUtils#writeField(Object)} for serialization</li>
 *   <li>{@link NbtUtils#readField(Tag)} for deserialization</li>
 * </ul>
 *
 * <p>Capable of handling all types supported by {@link NbtUtils}'s reflection-based system,
 * including primitives, collections, and arrays. For complex object graphs or
 * types requiring special handling (e.g., custom data validation), implement
 * a dedicated {@link INbtAdapter}.
 *
 * @see INbtAdapter#getAdapterFor(Class) Adapter resolution order
 * @see NbtUtils Supported type documentation
 */
public class GenericNbtAdapter implements INbtAdapter<Object> {

    /**
     * Specifies {@link Object} as the fallback target type, ensuring this adapter is used
     * only when no type-specific adapters are available.
     */
    @Override
    public Class<Object> getTargetType() {
        return Object.class;
    }

    /**
     * Serializes objects using {@link NbtUtils}'s reflection-based approach.
     *
     * @param value Non-null object to serialize. Must be of a type recognized by {@link NbtUtils}
     * @return Non-null NBT representation. Actual tag type depends on object structure
     * @throws IllegalArgumentException If {@link NbtUtils} cannot serialize the object
     */
    @Override
    public Tag save(Object value) {
        return NbtUtils.writeField(value);
    }

    /**
     * Deserializes objects using {@link NbtUtils}'s reflection-based approach.
     *
     * @param tag Non-null NBT data created by {@link #save(Object)}
     * @return Deserialized object instance, or null if the original serialized value was null
     * @throws ClassCastException If the tag type doesn't match the expected format for the
     *                            target Java type
     */
    @Override
    public Object load(Tag tag) {
        return NbtUtils.readField(tag);
    }
}