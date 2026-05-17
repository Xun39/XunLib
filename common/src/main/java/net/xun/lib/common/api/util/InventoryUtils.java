package net.xun.lib.common.api.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.inventory.ItemStackPredicate;
import net.xun.lib.common.api.inventory.InventoryCycleOrder;
import net.xun.lib.common.api.inventory.slot.SlotIterator;
import net.xun.lib.common.api.inventory.slot.SlotRange;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Universal inventory utilities for all container types. (sever-side only)
 * <p>
 * This class offers static methods for common inventory utils including:
 * <ul>
 *   <li>Item quantity checks</li>
 *   <li>Slot searching and item removal</li>
 *   <li>Inventory space verification</li>
 *   <li>Item collection with custom predicates</li>
 * </ul>
 *
 * @see ItemStackPredicate ItemStack predicates
 * @see EquipmentSlotsUtils Equipment slots-specific inventory utils
 */
public final class InventoryUtils {

    private InventoryUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    // ======================== CORE CHECKS ======================== //

    /**
     * Checks if a container contains at least {@code minCount} items matching the predicate.
     *
     * @param container Any inventory (player, chest, etc.)
     * @param predicate Item matching logic
     * @param minCount Minimum required items (≥1)
     * @param slots Optional slot range (null for entire inventory)
     * @return True if container contains sufficient matching items, false otherwise
     * @throws NullPointerException if container or predicate is null
     */
    public static boolean hasItemCount(Container container, ItemStackPredicate predicate, int minCount, @Nullable SlotRange slots) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        if (minCount < 1)
            throw new IllegalArgumentException("minCount must be ≥1");

        int count = 0;
        for (int slot : getSlotIterator(container, slots)) {
            ItemStack stack = container.getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                count += stack.getCount();

                if (count >= minCount)
                    return true;
            }
        }

        return false;
    }

    // ======================== SLOT SEARCHING ======================== //

    /**
     * Finds the first slot in the container that contains an item matching the predicate.
     *
     * @param container The container to search
     * @param predicate The predicate to test items
     * @param slots Optional slot range to restrict search (null for entire container)
     * @return Slot index of first match, or -1 if none
     * @throws NullPointerException if container or predicate is null
     */
    public static int findFirstMatchingSlot(Container container, ItemStackPredicate predicate, @Nullable SlotRange slots) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        for (int slot : getSlotIterator(container, slots)) {
            ItemStack stack = container.getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                return slot;
            }
        }

        return -1;
    }

    // ======================== ITEM MANAGEMENT ======================== //

    /**
     * Removes items from a container with slot priority control.
     * @param container Container to remove from
     * @param predicate Predicate to match items
     * @param amount Maximum number of items to remove
     * @param slots Optional slot range restriction
     * @param order Slot processing order strategy
     * @throws NullPointerException if container, predicate, or order is null
     */
    public static int extractItems(Container container, ItemStackPredicate predicate, int amount, @Nullable SlotRange slots, InventoryCycleOrder order) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        Objects.requireNonNull(order, "Removal order cannot be null");
        if (amount < 1) throw new IllegalArgumentException("Amount must be ≥1");

        List<Integer> slotOrder = order.getSlotOrder(container, slots);

        int remaining = amount;
        int removed = 0;
        boolean changed = false;

        for (int slot : slotOrder) {
            ItemStack stack = container.getItem(slot);

            if (stack.isEmpty() || !predicate.test(stack))
                continue;

            int remove = Math.min(stack.getCount(), remaining);
            stack.shrink(remove);

            removed += remove;
            remaining -= remove;
            changed = true;

            if (stack.isEmpty()) {
                container.setItem(slot, ItemStack.EMPTY);
            } else {
                container.setItem(slot, stack);
            }

            if (remaining <= 0)
                break;
        }

        if (changed)
            container.setChanged();

        return removed;
    }

    /**
     * Attempts to add an item stack to a container.
     * @param container Target inventory
     * @param stack Item stack to add (will not be modified)
     * @return Remaining items that couldn't be added (empty stack if all were added)
     * @throws NullPointerException if container or stack is null
     */
    public static ItemStack insertItem(Container container, ItemStack stack) {
        validateContainer(container);
        Objects.requireNonNull(stack, "ItemStack cannot be null");

        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = stack.copy();

        remaining = tryMergeWithExisting(container, remaining);

        if (remaining.isEmpty()) {
            container.setChanged();
            return ItemStack.EMPTY;
        }

        remaining = tryFillEmptySlots(container, remaining);
        container.setChanged();

        return remaining;
    }

    // ======================== UTILITY METHODS ======================== //

    /**
     * Collects copies of all item stacks matching the predicate.
     *
     * @param container Container to search
     * @param predicate Predicate to test items
     * @param slots Optional slot range restriction
     * @return Immutable list of matching item copies
     * @throws NullPointerException if container or predicate is null
     */
    public static ImmutableList<ItemStack> collectMatching(Container container, ItemStackPredicate predicate, @Nullable SlotRange slots) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");

        List<ItemStack> matches = new ArrayList<>();

        for (int slot : getSlotIterator(container, slots)) {
            ItemStack stack = container.getItem(slot);

            if (!stack.isEmpty() && predicate.test(stack)) {
                matches.add(stack.copy());
            }
        }

        return ImmutableList.copyOf(matches);
    }

    /**
     * Calculates total available space of a container
     *
     * @param container Container to check
     * @return The number of empty slots
     * @throws NullPointerException if container or stack is null
     */
    public static int getEmptySlotCount(Container container) {
        validateContainer(container);

        int count = 0;

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            if (container.getItem(slot).isEmpty()) {
                count += 1;
            }
        }

        return count;
    }

    // ======================== HELPER METHODS ======================== //

    private static Iterable<Integer> getSlotIterator(Container container, @Nullable SlotRange range) {
        return range != null
                ? range.getSlots(container)
                : () -> new SlotIterator(0, container.getContainerSize());
    }

    private static ItemStack tryMergeWithExisting(Container container, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);

            if (existing.isEmpty()) {
                continue;
            }

            if (!container.canPlaceItem(slot, remaining)) {
                continue;
            }

            if (!ItemStack.isSameItemSameComponents(existing, remaining)) {
                continue;
            }

            int maxStackSize = Math.min(
                    existing.getMaxStackSize(),
                    container.getMaxStackSize()
            );

            int availableSpace = maxStackSize - existing.getCount();

            if (availableSpace <= 0) {
                continue;
            }

            int transfer = Math.min(remaining.getCount(), availableSpace);

            ItemStack updated = existing.copy();
            updated.grow(transfer);

            container.setItem(slot, updated);

            remaining.shrink(transfer);

            if (remaining.isEmpty())
                return ItemStack.EMPTY;
        }

        return remaining;
    }

    private static ItemStack tryFillEmptySlots(Container container, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);

            if (!existing.isEmpty()) {
                continue;
            }

            if (!container.canPlaceItem(slot, remaining)) {
                continue;
            }

            int maxInsert = Math.min(
                    remaining.getMaxStackSize(),
                    container.getMaxStackSize()
            );

            int transfer = Math.min(remaining.getCount(), maxInsert);

            ItemStack newStack = remaining.copy();
            newStack.setCount(transfer);

            container.setItem(slot, newStack);

            remaining.shrink(transfer);

            if (remaining.isEmpty()) {
                return ItemStack.EMPTY;
            }
        }

        return remaining;
    }

    /**
     * Validates container accessibility and integrity.
     *
     * @param container Container to validate
     * @param allowClientSide Allow client-side container access
     * @throws NullPointerException if container is null
     * @throws IllegalStateException if client-side modifications are disallowed
     * @throws IllegalArgumentException for invalid container states
     */
    public static void validateContainer(Container container, boolean allowClientSide) {
        Objects.requireNonNull(container, "Container cannot be null");

        Level level = null;

        if (container instanceof Entity entity) {
            level = entity.level();
        } else if (container instanceof BlockEntity blockEntity) {
            level = blockEntity.getLevel();
        }

        if (!allowClientSide && level != null && level.isClientSide) {
            throw new IllegalStateException("Client-side inventory modifications are not allowed");
        }
    }

    /**
     * Validates a container while disallowing client-side access.
     *
     * @param container Container to validate
     */
    public static void validateContainer(Container container) {
        validateContainer(container, false);
    }
}
