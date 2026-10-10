package net.xun.lib.common.api.util;

import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.block.entity.container.ContainerSlotRange;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class InventoryUtil {
    private InventoryUtil() {
    }

    public static boolean hasItemCount(ContainerSlotRange slots, Predicate<ItemStack> predicate, int minCount) {
        Objects.requireNonNull(slots, "slots cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        if (minCount <= 0) return true;

        int count = 0;
        for (int slot : slots) {
            ItemStack stack = slots.getItem(slot);

            if (stack.isEmpty() || !predicate.test(stack))
                return false;

            count += stack.getCount();
            if (count >= minCount) return true;
        }

        return false;
    }

    public static int findFirstMatchingSlot(ContainerSlotRange slots, Predicate<ItemStack> predicate) {
        Objects.requireNonNull(slots, "slots cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        for (int slot : slots) {
            ItemStack stack = slots.getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return slot;
            }
        }

        return -1;
    }

    public static int extractItems(ContainerSlotRange slots, Predicate<ItemStack> predicate, int amount) {
        Objects.requireNonNull(slots, "slots cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        if (amount <= 0) return -1;

        int removed = 0;
        for (int slot : slots) {
            ItemStack stack = slots.getItem(slot);

            if (stack.isEmpty() || !predicate.test(stack))
                continue;

            int remove = stack.getCount();
            stack.shrink(remove);

            removed += remove;

            if (amount - remove <= 0) break;
        }

        // removed amount always bigger than 0
        slots.getContainer().setChanged();

        return removed;
    }

    /**
     * Attempts to add an item stack to a container
     * @param container the target container
     * @param stack the stack to add
     * @return remaining items that could not be added (empty if all were added)
     */
    public static ItemStack tryInsertStack(Container container, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int pass = 0; pass < 2; pass++) {
            boolean fillEmpty = pass == 1;

            for (int slot = 0; slot < container.getContainerSize(); slot++) {
                ItemStack existing = container.getItem(slot);
                boolean empty = existing.isEmpty();

                if (fillEmpty ? !empty : empty || !ItemStack.isSameItemSameComponents(existing, remaining)) {
                    continue;
                }

                if (!container.canPlaceItem(slot, remaining)) {
                    continue;
                }

                int currentCount = existing.getCount();
                int maxStackSize = Math.min(empty ? remaining.getMaxStackSize() : existing.getMaxStackSize(), container.getMaxStackSize());

                int transfer = Math.min(remaining.getCount(), maxStackSize - currentCount);

                if (transfer <= 0) continue;

                ItemStack updated = empty ? remaining.copy() : existing.copy();
                updated.setCount(currentCount + transfer);

                container.setItem(slot, updated);
                remaining.shrink(transfer);

                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return remaining;
    }


    public static List<ItemStack> collectMatching(ContainerSlotRange slots, Predicate<ItemStack> predicate) {
        Objects.requireNonNull(slots, "slots cannot be null");
        Objects.requireNonNull(predicate, "predicate cannot be null");

        List<ItemStack> matches = new ArrayList<>();
        for (int slot : slots) {
            ItemStack stack = slots.getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                matches.add(stack.copy());
            }
        }

        return List.copyOf(matches);
    }

    public static int getEmptySlotCount(Container container) {
        Objects.requireNonNull(container, "container cannot be null");

        int count = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty()) {
                count += 1;
            }
        }

        return count;
    }
}
