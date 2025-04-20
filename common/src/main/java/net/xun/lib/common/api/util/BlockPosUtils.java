package net.xun.lib.common.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.List;

public class BlockPosUtils {

    public static List<BlockPos> getDisc(BlockPos center, int radius) {
        List<BlockPos> disc = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, 0, -radius),
                center.offset(radius, 0, radius)
        ).filter(pos -> isWithinSphere(pos, center, radius))
                .forEach(pos -> disc.add(pos.immutable()));
        return disc;
    }

    public static List<BlockPos> getSquare(BlockPos center, int radius) {
        List<BlockPos> square = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, 0, -radius),
                center.offset(radius, 0 ,radius)
        ).filter(pos -> isWithinCube(pos, center, radius))
                .forEach(pos -> square.add(pos));
        return square;
    }

    public static List<BlockPos> getSphere(BlockPos center, int radius) {
        List<BlockPos> sphere = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        ).filter(pos -> isWithinSphere(pos, center, radius))
                .forEach(pos -> sphere.add(pos.immutable()));
        return sphere;
    }

    public static List<BlockPos> getHollowCube(BlockPos center, int radius) {
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos.betweenClosedStream(
                center.offset(-radius, -radius, -radius),
                center.offset(radius, radius, radius)
        ).filter(pos -> isWithinCube(pos, center, radius))
                .forEach(pos -> blocks.add(pos.immutable()));
        return blocks;
    }

    public static boolean isWithinSphere(BlockPos a, BlockPos b, double radius) {
        return a.distSqr(b) <= radius * radius;
    }

    public static boolean isWithinCube(BlockPos pos, BlockPos center, int radius) {
        return Math.abs(pos.getX() - center.getX()) <= radius
                && Math.abs(pos.getY() - center.getY()) <= radius
                && Math.abs(pos.getZ() - center.getZ()) <= radius;
    }

    public static BlockPos move(BlockPos pos, Direction dir, int steps) {
        return pos.offset(dir.getNormal().multiply(steps));
    }
}
