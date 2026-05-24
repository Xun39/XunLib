package net.xun.lib.common.api.util;

/**
 * Represents a rectangular area with position and dimensions.
 * <p>
 * This record is typically used for defining clickable or hoverable regions in UI screens,
 * with support for translating the area's origin by an offset (e.g., screen position of a container).
 * </p>
 *
 * @param x      the local X coordinate of the area's top-left corner (relative to some reference, e.g., a GUI component)
 * @param y      the local Y coordinate of the area's top-left corner
 * @param width  the width of the area (must be non-negative)
 * @param height the height of the area (must be non-negative)
 */
public record Area(int x, int y, int width, int height) {

    /**
     * Checks whether a given screen point lies inside this area after applying an offset.
     * <p>
     * The actual screen bounds are computed as:
     * <pre>
     * screenX = leftPos + x
     * screenY = topPos + y
     * </pre>
     *
     * @param mouseX the X coordinate of the point to test (usually in screen space)
     * @param mouseY the Y coordinate of the point to test
     * @param leftPos the horizontal offset added to this area's X (e.g., the left edge of a parent GUI)
     * @param topPos  the vertical offset added to this area's Y (e.g., the top edge of a parent GUI)
     * @return {@code true} if the point lies within the translated area; {@code false} otherwise
     */
    public boolean contains(int mouseX, int mouseY, int leftPos, int topPos) {
        int screenX = leftPos + x;
        int screenY = topPos + y;

        return mouseX >= screenX && mouseX < screenX + width &&
                mouseY >= screenY && mouseY < screenY + height;
    }
}
