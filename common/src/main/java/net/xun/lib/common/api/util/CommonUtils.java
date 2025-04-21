package net.xun.lib.common.api.util;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;
import net.xun.lib.common.internal.misc.ModIDManager;

import java.util.HashMap;
import java.util.Map;

public class CommonUtils {

    /**
     * Combines a namespace with path components into a namespaced ID string.
     * <p>
     * The path components are joined with underscores, forming the path part of the ID.
     * The result is formatted as {@code "namespace:joined_path"}. For example:
     * <pre>{@code
     * combineAsNamespacedID("mymod", "item", "example") // returns "mymod:item_example"
     * }</pre>
     *
     * @param namespace The namespace to use (typically a mod ID)
     * @param pathParts The components of the path to join with underscores
     * @return The combined namespaced ID in standard "namespace:path" format
     */
    public static String combineAsNamespacedID(String namespace, String... pathParts) {
        return "%s%s".formatted(namespace, String.join("_", pathParts));
    }

    /**
     * Creates a ResourceLocation using the auto-detected or manually set mod ID.
     *
     * @param path The resource path (e.g., "items/example")
     * @return Namespaced ResourceLocation
     * @throws IllegalStateException If the mod ID has not been set or detected
     */
    public static ResourceLocation modResourceLocation(String path) {
        return ResourceLocation.fromNamespaceAndPath(ModIDManager.getModId(), path);
    }

    /**
     * Generates a namespaced ID string using the current mod ID.
     *
     * @param pathParts The ID path components to join with underscores
     * @return Combined ID string in "namespace:path" format
     * @throws IllegalStateException If the mod ID has not been set or detected
     */
    public static String namespacedID(String... pathParts) {
        return combineAsNamespacedID(ModIDManager.getModId(), pathParts);
    }

    private static final Map<Class<?>, Registry<?>> REGISTRIES = new HashMap<>();

    static {
        REGISTRIES.put(SoundEvent.class, BuiltInRegistries.SOUND_EVENT);
        REGISTRIES.put(Fluid.class, BuiltInRegistries.FLUID);
        REGISTRIES.put(MobEffect.class, BuiltInRegistries.MOB_EFFECT);
        REGISTRIES.put(Block.class, BuiltInRegistries.BLOCK);
        REGISTRIES.put(EntityType.class, BuiltInRegistries.ENTITY_TYPE);
        REGISTRIES.put(Item.class, BuiltInRegistries.ITEM);
        REGISTRIES.put(Potion.class, BuiltInRegistries.POTION);
        REGISTRIES.put(ParticleType.class, BuiltInRegistries.PARTICLE_TYPE);
        REGISTRIES.put(BlockEntityType.class, BuiltInRegistries.BLOCK_ENTITY_TYPE);
        REGISTRIES.put(ArmorMaterial.class, BuiltInRegistries.ARMOR_MATERIAL);
    }

    /**
     * Retrieves the registry key for a given object by checking known registries.
     * <p>
     * Supports objects registered in common registries such as blocks, items, entities,
     * effects, and more. If the object's type isn't recognized, throws an exception.
     * </p>
     *
     * @param obj The registered object to get the key for
     * @return The resource location key of the object
     * @throws IllegalArgumentException If the object's type isn't in the supported registries
     */
    public static ResourceLocation getKey(Object obj) {
        for (Map.Entry<Class<?>, Registry<?>> entry : REGISTRIES.entrySet()) {
            if (entry.getKey().isInstance(obj)) {
                Registry<Object> registry = (Registry<Object>) entry.getValue();
                return registry.getKey(obj);
            }
        }
        throw new IllegalArgumentException("Unsupported type: " + obj.getClass());
    }

    /**
     * Gets the path component of the registry key for the given object.
     * <p>
     * Equivalent to calling {@code getKey(obj).getPath()}.
     * </p>
     *
     * @param obj The registered object to get the ID for
     * @return The path portion of the object's registry key
     * @throws IllegalArgumentException If the object's type isn't supported
     * @see #getKey(Object)
     */
    public static String getRegistryID(Object obj) {
        return getKey(obj).getPath();
    }

    /**
     * Gets the path component of the registry key for an {@link ItemLike} object.
     * <p>
     * Specifically handles items and blocks through the {@link ItemLike} interface.
     * </p>
     *
     * @param itemLike The item or block to get the registry ID for
     * @return The path portion of the object's registry key
     * @throws IllegalArgumentException If the object's type isn't supported
     * @see #getKey(Object)
     */
    public static String getRegistryID(ItemLike itemLike) {
        return getKey(itemLike).getPath();
    }
}
