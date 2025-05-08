package net.xun.lib.common.api.annotations;

import net.xun.lib.common.api.nbt.INbtAdapter;
import net.xun.lib.common.api.nbt.adapters.GenericNbtAdapter;
import net.xun.lib.common.internal.misc.NbtUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking fields for automatic NBT serialization/deserialization processing.
 * <p>
 * Fields annotated with {@code @PersistentNbt} participate in reflection-based NBT conversion
 * through {@link NbtUtils}. Supports both default reflection-based handling and custom
 * serialization logic via {@link INbtAdapter} implementations.
 *
 * <p><b>Key Characteristics:</b>
 * <ul>
 *   <li>Runtime retention for reflection access</li>
 *   <li>Field-only targeting (other element types ignored)</li>
 *   <li>Thread-safe when used with properly synchronized {@link NbtUtils} methods</li>
 * </ul>
 *
 * <p><b>Example:</b>
 * <pre>{@code
 * @PersistentNbt
 * private int smeltingTime;  // Auto-serialized using GenericNbtAdapter
 *
 * @PersistentNbt(adapter = BlockPosAdapter.class)
 * private BlockPos cachedPosition;  // Custom serialization
 * }</pre>
 *
 * @see NbtUtils#writeField(Object) Serialization entry point
 * @see INbtAdapter Custom serialization logic
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PersistentNbt {

    /**
     * Specifies a custom serialization strategy for the annotated field.
     * <p>
     * Defaults to {@link GenericNbtAdapter} which uses reflection-based serialization
     * for common Java types. Use custom adapters for:
     * <ul>
     *   <li>Legacy data format compatibility</li>
     *   <li>Optimized handling of complex data structures</li>
     *   <li>Types lacking default constructors</li>
     *   <li>Third-party classes without source access</li>
     * </ul>
     *
     * <p><b>Implementation Note:</b> The adapter must declare a compatible target type
     * through {@link INbtAdapter#getTargetType()} matching the field's declared type.
     *
     * <p><b>Example:</b>
     * <pre>{@code
     * // Custom adapter for inventory serialization
     * @PersistentNbt(adapter = ItemStackAdapter.class)
     * private NonNullList<ItemStack> items;
     * }</pre>
     */
    Class<? extends INbtAdapter> adapter() default GenericNbtAdapter.class;
}
