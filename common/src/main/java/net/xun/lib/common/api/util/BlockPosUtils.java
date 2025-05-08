package net.xun.lib.common.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.xun.lib.common.api.exceptions.UtilityClassException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BlockPosUtils {

    private BlockPosUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Generates a 2D circular disc of BlockPos around a center point at the same Y level.
     *
     * @param center  The center position of the disc
     * @param radius  The radius of the disc in blocks
     * @return List of BlockPos forming a horizontal circular pattern
     */
    public static List<BlockPos> getDisc(BlockPos center, int radius) {
        List<BlockPos> disc = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, 0, -radius),
                center.offset(radius, 0, radius)
        ).filter(pos -> isWithinSphere(pos, center, radius))
                .forEach(pos -> disc.add(pos.immutable()));
        return disc;
    }

    /**
     * Generates a 2D square of BlockPos around a center point at the same Y level.
     *
     * @param center  The center position of the square
     * @param radius  The distance from center to edge in blocks
     * @return List of BlockPos forming a horizontal square pattern
     */
    public static List<BlockPos> getSquare(BlockPos center, int radius) {
        List<BlockPos> square = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, 0, -radius),
                center.offset(radius, 0 ,radius)
        ).filter(pos -> isWithinCube(pos, center, radius))
                .forEach(pos -> square.add(pos));
        return square;
    }

    /**
     * Generates a 3D sphere of BlockPos around a center point.
     *
     * @param center  The center position of the sphere
     * @param radius  The radius of the sphere in blocks
     * @return List of BlockPos forming a spherical pattern
     */
    public static List<BlockPos> getSphere(BlockPos center, int radius) {
        List<BlockPos> sphere = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        ).filter(pos -> isWithinSphere(pos, center, radius))
                .forEach(pos -> sphere.add(pos.immutable()));
        return sphere;
    }

    /**
     * Generates positions for a hollow cubic frame around a center point.
     *
     * @param center  The center position of the cube
     * @param radius  The distance from center to any face in blocks
     * @return List of BlockPos forming the surface of a cube
     */
    public static List<BlockPos> getHollowCube(BlockPos center, int radius) {
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        ).filter(pos -> isWithinCube(pos, center, radius))
                .forEach(pos -> blocks.add(pos.immutable()));
        return blocks;
    }

    /**
     * Checks if a position is within a spherical radius of another position.
     *
     * @param a       Position to check
     * @param b       Center position of the sphere
     * @param radius  Sphere radius in blocks
     * @return True if position is within or on the sphere surface
     */
    public static boolean isWithinSphere(BlockPos a, BlockPos b, double radius) {
        return a.distSqr(b) <= radius * radius;
    }

    /**
     * Checks if a position is within a cubic area around a center.
     *
     * @param pos     Position to check
     * @param center  Center of the cube
     * @param radius  Cube radius (distance from center to any face)
     * @return True if position is within or on the cube surface
     */
    public static boolean isWithinCube(BlockPos pos, BlockPos center, int radius) {
        return Math.abs(pos.getX() - center.getX()) <= radius
                && Math.abs(pos.getY() - center.getY()) <= radius
                && Math.abs(pos.getZ() - center.getZ()) <= radius;
    }

    /**
     * Moves a position in a specific direction.
     *
     * @param pos    Starting position
     * @param dir    Direction to move
     * @param steps  Number of blocks to move
     * @return New position after movement
     */
    public static BlockPos move(BlockPos pos, Direction dir, int steps) {
        return pos.offset(dir.getNormal().multiply(steps));
    }

    /**
     * Creates an AABB centered at the specified BlockPos with given radii in each axis.
     *
     * @param center   The center point of the AABB.
     * @param xRadius  The radius along the X-axis.
     * @param yRadius  The radius along the Y-axis.
     * @param zRadius  The radius along the Z-axis.
     * @return A new AABB centered at the given position with the specified radii.
     */
    public static AABB createAABBFromCenter(BlockPos center, double xRadius, double yRadius, double zRadius) {
        double x = center.getX();
        double y = center.getY();
        double z = center.getZ();
        return new AABB(x - xRadius, y - yRadius, z - zRadius, x + xRadius, y + yRadius, z + zRadius);
    }

    /**
     * Creates an AABB centered at the specified BlockPos with given radius.
     *
     * @param center The center point of the AABB.
     * @param radius The radius.
     * @return A new AABB centered at the given position with the specified radius.
     */
    public static AABB createAABBFromCenter(BlockPos center, double radius) {
        return createAABBFromCenter(center, radius, radius, radius);
    }

    /**
     * Creates an AABB spanning between two BlockPos corners (inclusive).
     *
     * @param corner1  First corner of the AABB.
     * @param corner2  Second corner of the AABB.
     * @return An AABB covering all blocks between the corners, including their edges.
     */
    public static AABB createAABBFromCorners(BlockPos corner1, BlockPos corner2) {
        int minX = Math.min(corner1.getX(), corner2.getX());
        int minY = Math.min(corner1.getY(), corner2.getY());
        int minZ = Math.min(corner1.getZ(), corner2.getZ());
        int maxX = Math.max(corner1.getX(), corner2.getX());
        int maxY = Math.max(corner1.getY(), corner2.getY());
        int maxZ = Math.max(corner1.getZ(), corner2.getZ());
        return new AABB(minX, minY, minZ, maxX + 1.0, maxY + 1.0, maxZ + 1.0);
    }

    /**
     * Retrieves all BlockPos that intersect with the given AABB.
     *
     * @param aabb  The AABB to check for intersections.
     * @return List of BlockPos that intersect the AABB.
     */
    public static List<BlockPos> getBlocksInAABB(AABB aabb) {
        int minX = Mth.floor(aabb.minX);
        int minY = Mth.floor(aabb.minY);
        int minZ = Mth.floor(aabb.minZ);
        int maxX = Mth.floor(aabb.maxX - 1.0E-7D);
        int maxY = Mth.floor(aabb.maxY - 1.0E-7D);
        int maxZ = Mth.floor(aabb.maxZ - 1.0E-7D);

        List<BlockPos> blocks = new ArrayList<>();
        BlockPos.betweenClosedStream(minX, minY, minZ, maxX, maxY, maxZ).forEach(pos -> blocks.add(pos.immutable()));
        return blocks;
    }

    /**
     * Checks if a BlockPos's corresponding block area intersects with an AABB.
     *
     * @param aabb  The AABB to check against.
     * @param pos   The BlockPos to test.
     * @return True if the block's AABB intersects the given AABB.
     */
    public static boolean doesAABBIntersectBlock(AABB aabb, BlockPos pos) {
        AABB blockAABB = new AABB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0, pos.getZ() + 1.0);
        return aabb.intersects(blockAABB);
    }

    /**
     * Expands an AABB by specified amounts in each direction.
     *
     * @param original  The original AABB.
     * @param x         Amount to expand along the X-axis (both directions).
     * @param y         Amount to expand along the Y-axis (both directions).
     * @param z         Amount to expand along the Z-axis (both directions).
     * @return A new expanded AABB.
     */
    public static AABB expandAABB(AABB original, double x, double y, double z) {
        return original.inflate(x, y, z);
    }

    /**
     * Expands an AABB by specified amounts in each direction.
     *
     * @param original The original AABB.
     * @param amount   Amount to expand.
     * @return A new expanded AABB.
     */
    public static AABB expandAABB(AABB original, double amount) {
        return expandAABB(original, amount, amount, amount);
    }

    /**
     * Computes the intersection of two AABBs if they overlap.
     *
     * @param a  First AABB.
     * @param b  Second AABB.
     * @return Optional containing the intersecting AABB, or empty if no overlap.
     */
    public static Optional<AABB> getIntersectionAABB(AABB a, AABB b) {
        if (!a.intersects(b)) {
            return Optional.empty();
        }
        double minX = Math.max(a.minX, b.minX);
        double minY = Math.max(a.minY, b.minY);
        double minZ = Math.max(a.minZ, b.minZ);
        double maxX = Math.min(a.maxX, b.maxX);
        double maxY = Math.min(a.maxY, b.maxY);
        double maxZ = Math.min(a.maxZ, b.maxZ);
        return Optional.of(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    /**
     * Computes the union AABB that fully contains both input AABBs.
     *
     * @param a  First AABB.
     * @param b  Second AABB.
     * @return A new AABB encompassing both input AABBs.
     */
    public static AABB getUnionAABB(AABB a, AABB b) {
        double minX = Math.min(a.minX, b.minX);
        double minY = Math.min(a.minY, b.minY);
        double minZ = Math.min(a.minZ, b.minZ);
        double maxX = Math.max(a.maxX, b.maxX);
        double maxY = Math.max(a.maxY, b.maxY);
        double maxZ = Math.max(a.maxZ, b.maxZ);
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
