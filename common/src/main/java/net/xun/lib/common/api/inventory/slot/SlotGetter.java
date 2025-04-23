package net.xun.lib.common.api.inventory.slot;

import net.xun.lib.common.api.inventory.PlayerInventorySection;

/**
 * Utility class for calculating inventory slot indices based on grid coordinates (rows/columns),
 * inventory sections, and container-to-inventory conversions.
 */
public class SlotGetter {

    /**
     * Converts GUI grid coordinates (row and column) to the corresponding container slot index.
     *
     * @param row       the row in the GUI, starting from 0
     * @param column    the column in the GUI, starting from 0
     * @param slotsPerRow  the number of slots per row in the container's GUI
     * @return the calculated slot index
     * @throws IllegalArgumentException if guiColumn is out of bounds for the given slotsPerRow
     */
    public static int getContainerSlot(int row, int column, int slotsPerRow) {
        if (column < 0 || column >= slotsPerRow) {
            throw new IllegalArgumentException("guiColumn must be between 0 and " + (slotsPerRow - 1));
        }
        return row * slotsPerRow + column;
    }

    /**
     * Converts grid coordinates (row and column) within a specific inventory section to the corresponding inventory slot index.
     *
     * @param row     the row within the inventory section
     * @param column  the column within the inventory section
     * @param section the inventory section (HOTBAR, ARMOR, MAIN_INVENTORY)
     * @return the calculated inventory slot index
     * @throws IllegalArgumentException if the row or column is invalid for the given section
     */
    public static int getInventorySlotIndex(int row, int column, PlayerInventorySection section) {
        switch (section) {
            case HOTBAR:
                if (row != 0) {
                    throw new IllegalArgumentException("HOTBAR section only has row 0.");
                }
                if (column < 0 || column >= 9) {
                    throw new IllegalArgumentException("HOTBAR column must be 0-8.");
                }
                return section.getStart() + column;
            case MAIN_INVENTORY:
                if (row < 0 || row >= 3) {
                    throw new IllegalArgumentException("MAIN_INVENTORY row must be 0-2.");
                }
                if (column < 0 || column >= 9) {
                    throw new IllegalArgumentException("MAIN_INVENTORY column must be 0-8.");
                }
                return section.getStart() + (row * 9) + column;
            case ARMOR:
                if (column != 0) {
                    throw new IllegalArgumentException("ARMOR section only has column 0.");
                }
                if (row < 0 || row >= 4) {
                    throw new IllegalArgumentException("ARMOR row must be 0-3.");
                }
                return section.getStart() + row;
            default:
                throw new IllegalArgumentException("Unsupported InventorySection: " + section);
        }
    }

    /**
     * Returns the hotbar slot index corresponding to the given hotbar number.
     *
     * @param number the hotbar number (0-8)
     * @return the hotbar slot index
     * @throws IllegalArgumentException if the number is outside the valid range
     */
    public static int getHotbarSlotIndex(int number) {
        if (number < 0 || number >= 9) {
            throw new IllegalArgumentException("Hotbar number must be between 0 and 8.");
        }
        return number;
    }
}