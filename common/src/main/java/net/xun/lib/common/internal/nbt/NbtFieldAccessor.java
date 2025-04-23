package net.xun.lib.common.internal.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtException;
import net.minecraft.nbt.Tag;
import net.xun.lib.common.api.annotations.PersistentNbt;
import net.xun.lib.common.api.nbt.INbtAdapter;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * Runtime field accessor for {@link PersistentNbt} annotated fields.
 * <p>
 * Wraps reflection operations with MethodHandles for better performance
 * and integrates with the {@link INbtAdapter} system.
 *
 */
@ApiStatus.Internal
public class NbtFieldAccessor {
    private final Field field;
    private final MethodHandle getter;
    private final MethodHandle setter;
    private final INbtAdapter<Object> adapter;

    @SuppressWarnings("unchecked")
    public NbtFieldAccessor(Field field) {
        this.field = field;
        field.setAccessible(true);
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            this.getter = lookup.unreflectGetter(field);
            this.setter = lookup.unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field: " + field.getName(), e);
        }

        Class<?> fieldType = field.getType();
        this.adapter = (INbtAdapter<Object>) INbtAdapter.getAdapterFor(fieldType);
    }

    /**
     * Writes a field's value to NBT using its configured adapter
     * @param instance Containing object instance
     * @param tag Target compound tag
     */
    public void write(Object instance, CompoundTag tag) {
        try {
            Object value = getter.invoke(instance);
            Tag savedTag = adapter.save(value);
            tag.put(field.getName(), savedTag);
        } catch (Throwable e) {
            throw new NbtException("Failed to write field: " + field.getName());
        }
    }

    /**
     * Reads a field's value from NBT using its configured adapter
     * @param instance Target object to modify
     * @param tag Source compound tag
     */
    public void load(Object instance, CompoundTag tag) {
        try {
            String fieldName = field.getName();
            if (tag.contains(fieldName)) {
                Tag valueTag = tag.get(fieldName);
                Object value = adapter.load(valueTag);
                setter.invoke(instance, value);
            }
        } catch (Throwable e) {
            throw new NbtException("Failed to load field: " + field.getName());
        }
    }

    public Field getField() {
        return field;
    }

    public MethodHandle getGetter() {
        return this.getter;
    }

    public MethodHandle getSetter() {
        return setter;
    }

    public INbtAdapter<Object> getAdapter() {
        return adapter;
    }
}
