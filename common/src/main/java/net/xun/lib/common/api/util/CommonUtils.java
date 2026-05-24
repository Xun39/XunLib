package net.xun.lib.common.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.impl.ModIDManager;

public final class CommonUtils {
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
        return "%s:%s".formatted(namespace, String.join("_", pathParts));
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
