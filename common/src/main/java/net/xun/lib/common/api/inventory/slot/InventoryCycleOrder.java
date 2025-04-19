package net.xun.lib.common.api.inventory.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.xun.lib.common.api.util.InventoryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public enum InventoryCycleOrder {

    /** Slots 0 → max (standard iteration) */
    FORWARD,
    /** Slots max → 0 (reverse iteration) */
    REVERSE,
    /** Hotbar (0-8) → Main inventory (9-35) → Offhand (40) */
    PLAYER_PRIORITY,
    /** Armor (36-39) → Offhand (40) → Main inventory */
    ARMOR_FIRST;

    /**
     * Generates slot access order based on container type and range constraints
     * @param container Target inventory
     * @param range Optional slot restriction (null for full inventory)
     * @return List of slots in processing order
     */
    public List<Integer> getSlotOrder(Container container, @Nullable SlotRange range) {
        InventoryUtils.validateContainer(container, true); // Read-only validation

        // Determine effective slot range
        int minSlot = 0;
        int maxSlot = container.getContainerSize() - 1;

        if (range != null) {
            Iterator<Integer> rangeIter = range.getSlots(container).iterator();
            if (rangeIter.hasNext()) {
                minSlot = rangeIter.next();
                maxSlot = minSlot;
                while (rangeIter.hasNext()) {
                    maxSlot = rangeIter.next();
                }
            }
        }

        // Special handling for player inventories
        boolean isPlayerInventory = container instanceof Player;
        List<Integer> slots = new ArrayList<>();

        switch (this) {
            case FORWARD -> {
                for (int i = minSlot; i <= maxSlot; i++) slots.add(i);
            }
            case REVERSE -> {
                for (int i = maxSlot; i >= minSlot; i--) slots.add(i);
            }
            case PLAYER_PRIORITY -> {
                if (!isPlayerInventory) {
                    // Fallback to forward for non-player inventories
                    for (int i = minSlot; i <= maxSlot; i++) slots.add(i);
                    break;
                }

                // Player-specific priority order
                int hotbarEnd = Math.min(8, maxSlot);
                int mainInventoryEnd = Math.min(35, maxSlot);

                // 1. Hotbar (0-8)
                for (int i = Math.max(0, minSlot); i <= hotbarEnd; i++) slots.add(i);

                // 2. Main inventory (9-35)
                if (minSlot <= 35) {
                    for (int i = Math.max(9, minSlot); i <= mainInventoryEnd; i++) slots.add(i);
                }

                // 3. Offhand (40)
                if (maxSlot >= 40 && minSlot <= 40) slots.add(40);
            }
            case ARMOR_FIRST -> {
                if (!isPlayerInventory) {
                    for (int i = minSlot; i <= maxSlot; i++) slots.add(i);
                    break;
                }

                // Armor slots (36-39)
                for (int i = 36; i <= 39 && i <= maxSlot; i++) {
                    if (i >= minSlot) slots.add(i);
                }

                // Offhand (40)
                if (40 >= minSlot && 40 <= maxSlot) slots.add(40);

                // Remaining slots
                for (int i = minSlot; i <= maxSlot; i++) {
                    if ((i < 36 || i > 40) && !slots.contains(i)) {
                        slots.add(i);
                    }
                }
            }
        }

        return Collections.unmodifiableList(slots);
    }
}
