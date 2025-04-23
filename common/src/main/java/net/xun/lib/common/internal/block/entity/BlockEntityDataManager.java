package net.xun.lib.common.internal.block.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.xun.lib.common.api.annotations.PersistentNbt;
import net.xun.lib.common.internal.nbt.NbtFieldAccessor;
import org.jetbrains.annotations.ApiStatus;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central manager for block entity data operations.
 * <p>
 * This thread-safe class handles:
 * <ul>
 *   <li>Field scanning and reflection caching</li>
 *   <li>Coordinating {@link net.xun.lib.common.api.nbt.INbtAdapter} usage</li>
 *   <li>NBT read/write operations for {@link PersistentNbt} fields</li>
 * </ul>
 *
 */
@ApiStatus.Internal
public class BlockEntityDataManager {

    private static final Map<Class<?>, List<NbtFieldAccessor>> PERSIST_FIELDS = new ConcurrentHashMap<>();

    /**
     * Saves an object's state to NBT using annotated fields.
     * @param be The block entity that has the data to save.
     * @param tag Destination NBT compound tag
     */
    public static void savePersistedFields(BlockEntity be, CompoundTag tag) {
        Class<?> clazz = be.getClass();
        List<NbtFieldAccessor> fields = PERSIST_FIELDS.computeIfAbsent(clazz,
                k -> scanFields(clazz, PersistentNbt.class)
        );
        fields.forEach(f -> f.write(be, tag));
    }

    /**
     * Loads an object's state from NBT using annotated fields.
     * @param be Block entity to populate with persisted data
     * @param tag Source NBT compound tag
     */
    public static void loadPersistedFields(BlockEntity be, CompoundTag tag) {
        Class<?> clazz = be.getClass();
        List<NbtFieldAccessor> fields = PERSIST_FIELDS.computeIfAbsent(clazz,
                k -> scanFields(clazz, PersistentNbt.class)
        );
        fields.forEach(f -> f.load(be, tag));
    }

    public static List<NbtFieldAccessor> scanFields(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(annotation))
                .map(NbtFieldAccessor::new)
                .collect(Collectors.toList());
    }
}
