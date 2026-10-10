package net.xun.lib.common.api.block.entity.container.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TakeOnlySlot extends Slot {
    public TakeOnlySlot(Container pContainer, int pSlot, int pX, int pY) {
        super(pContainer, pSlot, pX, pY);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }
}
