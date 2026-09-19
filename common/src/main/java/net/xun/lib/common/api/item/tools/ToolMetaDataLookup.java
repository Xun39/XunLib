package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * A lookup utility for retrieving {@link ToolMetaData} associated with tool items.
 * <p>
 * This class stores metadata for tool items created via the Armory API, allowing
 * retrieval of piece type, context, and customizer from an {@link Item} or {@link ItemStack}.
 * </p>
 * <p>
 * The lookup uses identity-based mapping ({@link IdentityHashMap}) to avoid collisions
 * with item subclasses or overridden {@code equals}/{@code hashCode}.
 * </p>
 * <p>
 * This class is not intended to be instantiated.
 * </p>
 *
 * @since 3.0.0
 */
public final class ToolMetaDataLookup {

    private static final Map<Item, ToolMetaData> META_DATA_MAP = new IdentityHashMap<>();

    private ToolMetaDataLookup() {
    }

    /**
     * Registers tool metadata for the given item.
     *
     * @param item     the tool item to associate with metadata
     * @param instance the metadata instance (must not be {@code null})
     * @throws NullPointerException if either argument is {@code null}
     */
    public static void register(Item item, ToolMetaData instance) {
        META_DATA_MAP.put(item, instance);
    }

    /**
     * Retrieves the tool metadata for the given item.
     *
     * @param item the item to look up
     * @return the metadata, or {@code null} if not found
     */
    public static ToolMetaData get(Item item) {
        return META_DATA_MAP.get(item);
    }

    /**
     * Retrieves the tool metadata for the item in the given stack.
     *
     * @param stack the stack containing the item
     * @return the metadata, or {@code null} if not found
     */
    public static ToolMetaData get(ItemStack stack) {
        return get(stack.getItem());
    }

    /**
     * Checks whether the given item has associated tool metadata.
     *
     * @param item the item to check
     * @return {@code true} if metadata exists, {@code false} otherwise
     */
    public static boolean contains(Item item) {
        return META_DATA_MAP.containsKey(item);
    }

    /**
     * Checks whether the item in the given stack has associated tool metadata.
     *
     * @param stack the stack containing the item
     * @return {@code true} if metadata exists, {@code false} otherwise
     */
    public static boolean contains(ItemStack stack) {
        return contains(stack.getItem());
    }
}
