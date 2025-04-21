package net.xun.lib.common.internal.mixin;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.xun.lib.common.internal.block.entity.BlockEntityDataManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockEntity.class)
public abstract class BlockEntityMixin {

    @Inject(method = "saveAdditional", at = @At("HEAD"))
    private void onSave(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        BlockEntityDataManager.savePersistedFields((BlockEntity) (Object) this, tag);
    }

    @Inject(method = "loadAdditional", at = @At("HEAD"))
    private void onLoad(CompoundTag tag, HolderLookup.Provider registries, CallbackInfo ci) {
        BlockEntityDataManager.loadPersistedFields((BlockEntity) (Object) this, tag);
    }
}
