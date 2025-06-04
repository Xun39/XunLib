package net.xun.lib.common.api.util;

import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.internal.misc.ModIDManager;

import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;

public class CommonUtils {

    private CommonUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

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
    public static ResourceLocation modLoc(String path) {
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
    @SuppressWarnings("unchecked")
    public static ResourceLocation getKey(Object obj) {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            try {
                Registry<Object> objRegistry = (Registry<Object>) registry;
                ResourceLocation key = objRegistry.getKey(obj);
                if (key != null) {
                    return key;
                }
            } catch (ClassCastException ignored) {}
        }
        throw new IllegalArgumentException("Object not registered in any known registry: " + obj);
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

    /**
     * Creates a resource key from a registry and a path
     * @param registry Resource key of the registry
     * @param path The key path
     * @return A resource key from the registry and the path
     * @param <T> The type of the resource key
     */
    public static <T> ResourceKey<T> createKey(ResourceKey<? extends Registry<T>> registry, String path) {
        return ResourceKey.create(registry, CommonUtils.modLoc(path));
    }
}
