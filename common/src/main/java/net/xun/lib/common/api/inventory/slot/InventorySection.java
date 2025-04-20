package net.xun.lib.common.api.inventory.slot;

import java.util.function.IntPredicate;

/**
 * Represents inventory sections with dynamic slot ranges and validation.
 * <p>
 * Provides both predefined player inventory sections and tools for creating custom
 * slot ranges. Handles container size validation and inverted ranges automatically.
 * <p>
 * <strong>Predefined Sections (Minecraft Player Inventory):</strong>
 * <ul>
 *   <li>{@code HOTBAR} (0-8 inclusive)</li>
 *   <li>{@code MAIN_INVENTORY} (9-35 inclusive)</li>
 *   <li>{@code ARMOR} (36-39 inclusive)</li>
 *   <li>{@code OFFHAND} (slot 40)</li>
 *   <li>{@code ALL} (0-40 inclusive)</li>
 * </ul>
 */
public enum InventorySection {

    HOTBAR(0, 9),
    MAIN_INVENTORY(9, 36),
    ARMOR(36, 40),
    OFFHAND(40, 41),
    ALL(0, 41);

    private final int start;
    private final int end;
    private final IntPredicate checker;

    InventorySection(int start, int end) {
        this.start = start;
        this.end = end;
        this.checker = slot -> slot >= start && slot < end;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    /**
     * Creates a dynamic slot range that adapts to container size.
     */
    public SlotRange getSlotRange() {
        return new SlotRange(() -> start, () -> end);
    }

    /**
     * Checks if a slot belongs to this section in a standard player inventory.
     */
    public boolean matches(int slot) {
        return checker.test(slot);
    }
}