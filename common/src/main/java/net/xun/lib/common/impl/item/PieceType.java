package net.xun.lib.common.impl.item;

import org.jetbrains.annotations.ApiStatus;

/**
 * @since 2.0.0
 */
@ApiStatus.Internal
public interface PieceType {

    /**
     * Gets the naming suffix for this piece type.
     * <p>
     * The suffix is appended to a base name to create the full registry name
     * for an item. It should typically start with an underscore ("_") to
     * separate it from the base name, following Minecraft's naming conventions.
     * </p>
     * <p>
     * <strong>Examples:</strong>
     * </p>
     * <ul>
     *   <li>"_sword" for swords</li>
     *   <li>"_helmet" for helmets</li>
     *   <li>"_chestplate" for chestplates</li>
     * </ul>
     *
     * @return the naming suffix for this piece type, never {@code null}
     */
    String getNameSuffix();

    default String registryName(String setName) {
        return setName + getNameSuffix();
    }
}