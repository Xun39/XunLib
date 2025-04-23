package net.xun.lib.common.api.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.xun.lib.common.internal.block.entity.ClientTickable;
import net.xun.lib.common.internal.block.entity.ServerTickable;

public interface ITickableBlockEntity extends ClientTickable, ServerTickable {

    default void serverTick(Level level, BlockPos pos, BlockState state) {}
    default void clientTick(Level level, BlockPos pos, BlockState state) {}

    static <T extends BlockEntity> BlockEntityTicker<T> createTicker(BlockEntityType<?> expectedType) {
        return (level, pos, state, be) -> {
            if (be.getType() != expectedType) return;

            if (level.isClientSide()) {
                if (be instanceof ClientTickable ct) {
                    ct.clientTick(level, pos, state);
                }
            } else {
                if (be instanceof ServerTickable st) {
                    st.serverTick(level, pos, state);
                }
            }
        };
    }
}
