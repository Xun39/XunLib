package net.xun.lib.common.api.block.entity;

import com.mojang.datafixers.types.Type;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * A utility class that wraps the creation of {@link BlockEntityType} instances.
 * <p>
 * This class exists because {@link BlockEntityType.BlockEntitySupplier} is a private
 * interface in the common source set and therefore cannot be referenced directly from
 * cross-platform code. {@link BlockEntityFactory} is a functionally identical replacement
 * that can be used in its place.
 */
public final class BlockEntityTypeFactory {
    private BlockEntityTypeFactory() {}

    public static <T extends BlockEntity> BlockEntityType<T> create(BlockEntityFactory<T> factory, Type<?> pDataType, Block... validBlocks) {
        return BlockEntityType.Builder.of(factory::create, validBlocks).build(pDataType);
    }

    @FunctionalInterface
    public interface BlockEntityFactory<T extends BlockEntity> {
        T create(BlockPos pos, BlockState state);
    }
}
