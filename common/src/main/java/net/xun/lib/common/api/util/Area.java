package net.xun.lib.common.api.util;

import org.jetbrains.annotations.NotNull;

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
    public Area {
        if (width < 0) {
            throw new IllegalArgumentException("Area width must be non-negative: " + width);
        }
        if (height < 0) {
            throw new IllegalArgumentException("Area height must be non-negative: " + height);
        }
    }

    /**
     * Alias for the canonical constructor, for symmetry with {@link #fromCorners}.
     */
    public static Area of(int x, int y, int width, int height) {
        return new Area(x, y, width, height);
    }

    /**
     * Build an area from its four edges. Normalizes so width/height are non-negative.
     */
    public static Area fromCorners(int x1, int y1, int x2, int y2) {
        int nx = Math.min(x1, x2);
        int ny = Math.min(y1, y2);
        return new Area(nx, ny, Math.abs(x2 - x1), Math.abs(y2 - y1));
    }

    /**
     * Left edge. Alias for {@link #x()}; kept for Rect-style call sites.
     */
    public int x1() {
        return x;
    }

    /**
     * Top edge. Alias for {@link #y()}; kept for Rect-style call sites.
     */
    public int y1() {
        return y;
    }

    /**
     * Right edge (exclusive).
     */
    public int x2() {
        return x + width;
    }

    /**
     * Bottom edge (exclusive).
     */
    public int y2() {
        return y + height;
    }

    public int centerX() {
        return x + width / 2;
    }

    public int centerY() {
        return y + height / 2;
    }

    /**
     * True if this area has non-positive width or height.
     */
    public boolean isEmpty() {
        return width <= 0 || height <= 0;
    }

    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointX < x2() && pointY >= y && pointY < y2();
    }

    public boolean contains(int pointX, int pointY) {
        return pointX >= x && pointX < x2() && pointY >= y && pointY < y2();
    }

    /**
     * Tests a point against this area translated by an offset.
     *
     * <p>This is useful when the area is stored in local coordinates.</p>
     */
    public boolean contains(double pointX, double pointY, int offsetX, int offsetY) {
        return offset(offsetX, offsetY).contains(pointX, pointY);
    }

    /**
     * True if {@code other} lies entirely inside this area (local coords).
     */
    public boolean contains(Area other) {
        return other.x >= x && other.y >= y && other.x + other.width <= x + width && other.y + other.height <= y + height;
    }

    /**
     * True if the two areas overlap at all (local coords).
     */
    public boolean intersects(Area other) {
        return x < other.x + other.width && other.x < x + width && y < other.y + other.height && other.y < y + height;
    }

    public Area offset(int dx, int dy) {
        return new Area(x + dx, y + dy, width, height);
    }

    public Area offsetX(int dx) {
        return offset(dx, 0);
    }

    public Area offsetY(int dy) {
        return offset(0, dy);
    }

    public Area inset(int a) {
        return new Area(x + a, y + a, width - 2 * a, height - 2 * a);
    }

    public Area insetX(int a) {
        return new Area(x + a, y, width - 2 * a, height);
    }

    public Area insetY(int a) {
        return new Area(x, y + a, width, height - 2 * a);
    }

    public Area expand(int a) {
        return inset(-a);
    }

    public Area expandX(int a) {
        return insetX(-a);
    }

    public Area expandY(int a) {
        return insetY(-a);
    }

    public Area withX(int nx) {
        return new Area(nx, y, width, height);
    }

    public Area withY(int ny) {
        return new Area(x, ny, width, height);
    }

    public Area withWidth(int w) {
        return new Area(x, y, w, height);
    }

    public Area withHeight(int h) {
        return new Area(x, y, width, h);
    }

    /**
     * New area anchored at this one's top-left, extending {@code w} to the right.
     */
    public Area withLeftEdge(int w) {
        return new Area(x, y, w, height);
    }

    /**
     * New area anchored at this one's top-right, extending {@code w} to the left.
     */
    public Area withRightEdge(int w) {
        return new Area(x + width - w, y, w, height);
    }

    /**
     * Horizontal strip along the top of thickness {@code t}.
     */
    public Area topEdge(int t) {
        return new Area(x, y, width, t);
    }

    /**
     * Horizontal strip along the bottom of thickness {@code t}.
     */
    public Area bottomEdge(int t) {
        return new Area(x, y + height - t, width, t);
    }

    /**
     * Vertical strip along the left, inset by {@code t} from top and bottom.
     */
    public Area leftEdge(int t) {
        return new Area(x, y + t, t, height - 2 * t);
    }

    /**
     * Vertical strip along the right, inset by {@code t} from top and bottom.
     */
    public Area rightEdge(int t) {
        return new Area(x + width - t, y + t, t, height - 2 * t);
    }

    public Area before(int width, int gap) {
        return new Area(x - gap - width, y, width, height);
    }

    /**
     * Centers a rectangle of the requested size inside this area.
     */
    public Area centered(int childWidth, int childHeight) {
        childWidth = Math.min(childWidth, width);
        childHeight = Math.min(childHeight, height);

        return new Area(centerX() - childWidth / 2, centerY() - childHeight / 2, childWidth, childHeight);
    }

    /**
     * Intersection of this area with {@code outer}, clamped into {@code outer}.
     */
    public Area clampTo(Area outer) {
        int nx1 = Math.max(x, outer.x);
        int ny1 = Math.max(y, outer.y);
        int nx2 = Math.min(x + width, outer.x + outer.width);
        int ny2 = Math.min(y + height, outer.y + outer.height);
        return new Area(nx1, ny1, Math.max(0, nx2 - nx1), Math.max(0, ny2 - ny1));
    }

    @Override
    public @NotNull String toString() {
        return "Area[" + x1() + "," + y1() + " -> " + x2() + "," + y2() + " (" + width + "x" + height + ")]";
    }
}
