package net.xun.lib.common.internal.misc;

import net.minecraft.nbt.*;
import net.xun.lib.common.api.nbt.PersistentNbt;
import net.xun.lib.common.internal.nbt.NbtFieldAccessor;
import net.xun.lib.common.internal.block.entity.BlockEntityDataManager;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;

/**
 * Core NBT serialization utilities with extended type support and reflection capabilities.
 * <p>
 * Provides bidirectional conversion between Java objects and NBT structures, handling:
 * <ul>
 *   <li>Primitives and their boxed counterparts</li>
 *   <li>{@link String}, {@link UUID}, and NBT array types</li>
 *   <li>Common Java collections ({@link Iterable}, {@link Map} with string keys)</li>
 *   <li>Objects with fields annotated by {@link PersistentNbt}</li>
 * </ul>
 *
 * <p>Used in conjunction with {@link net.xun.lib.common.api.nbt.INbtAdapter} for custom serialization logic. When processing
 * custom objects, fields must be accessible and annotated with {@link PersistentNbt} to participate
 * in serialization. Reflection failures during custom object handling are silently ignored,
 * potentially resulting in {@link IllegalArgumentException} if no valid fields are found.
 */
@ApiStatus.Internal
public class NbtUtils {

    /**
     * Serializes an object to NBT with complete type preservation and structure.
     *
     * @param value Non-null object to serialize. Supported types include primitives,
     *        strings, UUIDs, arrays, collections, maps with string keys, and objects
     *        with {@link PersistentNbt} annotations
     * @return NBT representation of the input object
     * @throws IllegalArgumentException If null is provided, or if the type hierarchy
     *         contains unsupported types not handled by registered {@link net.xun.lib.common.api.nbt.INbtAdapter}s
     */
    public static Tag writeField(Object value) {
        if (value == null) {
            throw new IllegalArgumentException("Cannot serialize null values");
        }

        // Basic types
        if (value instanceof Number num) {
            switch (num) {
                case Integer i -> {
                    return IntTag.valueOf(num.intValue());
                }
                case Byte b -> {
                    return ByteTag.valueOf(num.byteValue());
                }
                case Short i -> {
                    return ShortTag.valueOf(num.shortValue());
                }
                case Long l -> {
                    return LongTag.valueOf(num.longValue());
                }
                case Float v -> {
                    return FloatTag.valueOf(num.floatValue());
                }
                case Double v -> {
                    return DoubleTag.valueOf(num.doubleValue());
                }
                default -> {
                }
            }
        }
        if (value instanceof Boolean bool) {
            return ByteTag.valueOf(bool ? (byte) 1 : (byte) 0);
        }
        if (value instanceof String str) {
            return StringTag.valueOf(str);
        }
        if (value instanceof UUID uuid) {
            CompoundTag uuidTag = new CompoundTag();
            uuidTag.putLong("MostSig", uuid.getMostSignificantBits());
            uuidTag.putLong("LeastSig", uuid.getLeastSignificantBits());
            return uuidTag;
        }

        // Arrays
        if (value instanceof byte[] bytes) return new ByteArrayTag(bytes);
        if (value instanceof int[] ints) return new IntArrayTag(ints);
        if (value instanceof long[] longs) return new LongArrayTag(longs);

        // Collections
        if (value instanceof Iterable<?> iterable) {
            ListTag list = new ListTag();
            for (Object element : iterable) {
                list.add(writeField(element));
            }
            return list;
        }

        // Maps with string keys
        if (value instanceof Map<?, ?> map) {
            CompoundTag compound = new CompoundTag();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                Object key = entry.getKey();
                if (!(key instanceof String)) {
                    throw new IllegalArgumentException("Map keys must be strings");
                }
                compound.put((String) key, writeField(entry.getValue()));
            }
            return compound;
        }

        // Custom objects with @PersistNbt fields
        try {
            CompoundTag objectTag = new CompoundTag();
            List<NbtFieldAccessor> fields = BlockEntityDataManager.scanFields(value.getClass(), PersistentNbt.class);
            for (NbtFieldAccessor field : fields) {
                Object fieldValue = field.getGetter().invoke(value);
                objectTag.put(field.getField().getName(), writeField(fieldValue));
            }
            return objectTag;
        } catch (Throwable ignored) {}

        throw new IllegalArgumentException("Unsupported type: " + value.getClass().getName());
    }

    /**
     * Reconstructs an object from NBT data with automatic type resolution.
     *
     * @param tag NBT data structure to deserialize. Must match the expected format
     *        for the corresponding Java type
     * @return Reconstructed Java object. Specific return types include:
     *         <ul>
     *           <li>Primitive wrappers for numeric tags</li>
     *           <li>{@link String} for string tags</li>
     *           <li>Arrays for array tags</li>
     *           <li>{@link HashMap} for compound tags (unless UUID structure detected)</li>
     *           <li>{@link ArrayList} for list tags</li>
     *         </ul>
     * @throws IllegalArgumentException If the tag structure doesn't match expected
     *         formats for type reconstruction
     */
    public static Object readField(Tag tag) {
        if (tag instanceof NumericTag num) {
            switch (tag) {
                case IntTag intTag -> {
                    return num.getAsInt();
                }
                case ByteTag byteTag -> {
                    return num.getAsByte();
                }
                case ShortTag shortTag -> {
                    return num.getAsShort();
                }
                case LongTag longTag -> {
                    return num.getAsLong();
                }
                case FloatTag floatTag -> {
                    return num.getAsFloat();
                }
                case DoubleTag doubleTag -> {
                    return num.getAsDouble();
                }
                default -> {
                }
            }
        }
        if (tag instanceof StringTag str) {
            return str.getAsString();
        }
        if (tag instanceof ByteArrayTag bat) {
            return bat.getAsByteArray();
        }
        if (tag instanceof IntArrayTag iat) {
            return iat.getAsIntArray();
        }
        if (tag instanceof LongArrayTag lat) {
            return lat.getAsLongArray();
        }
        if (tag instanceof CompoundTag compound) {
            if (compound.contains("MostSig", Tag.TAG_LONG) &&
                    compound.contains("LeastSig", Tag.TAG_LONG)) {
                return new UUID(
                        compound.getLong("MostSig"),
                        compound.getLong("LeastSig")
                );
            }

            Map<String, Object> map = new HashMap<>();
            for (String key : compound.getAllKeys()) {
                map.put(key, readField(compound.get(key)));
            }
            return map;
        }
        if (tag instanceof ListTag list) {
            List<Object> result = new ArrayList<>();
            for (Tag element : list) {
                result.add(readField(element));
            }
            return result;
        }

        throw new IllegalArgumentException("Unsupported tag type: " + tag.getClass().getName());
    }
}