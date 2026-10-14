package net.xun.lib.common.api.client.registration.context;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.world.level.block.Block;

@FunctionalInterface
public interface BlockColorContext {
    void register(BlockColor blockColor, Block... blocks);
}
