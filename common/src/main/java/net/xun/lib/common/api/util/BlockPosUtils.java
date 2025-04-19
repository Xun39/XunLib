package net.xun.lib.common.api.util;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class BlockPosUtils {

    public static List<BlockPos> getBlocksAround(BlockPos pos, int radius) {
        List<BlockPos> blocks = new ArrayList<>();
        BlockPos start = pos.offset(-radius, -radius, -radius);
        BlockPos end = pos.offset(radius, radius, radius);
        for (BlockPos blockPos : BlockPos.betweenClosed(start, end)) {
            blocks.add(blockPos.immutable());
        }
        return blocks;
    }
}
