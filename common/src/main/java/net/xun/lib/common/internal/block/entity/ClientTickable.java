package net.xun.lib.common.internal.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ClientTickable {
    void clientTick(Level level, BlockPos pos, BlockState state);
}
