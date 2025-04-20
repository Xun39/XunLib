package net.xun.lib.common.api.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;
import net.xun.lib.common.api.inventory.slot.InventoryCycleOrder;
import net.xun.lib.common.api.inventory.slot.InventorySection;
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
 * @see InventoryPredicate Inventory predicates
 * @see ArmorSlotsUtils Armor-specific inventory utils
 */
public class InventoryUtils {

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
    public static boolean hasItemCount(Container container, InventoryPredicate predicate, int minCount, @Nullable SlotRange slots) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        if (minCount < 1) throw new IllegalArgumentException("minCount must be ≥1");

        int count = 0;
        for (int slot : getSlotIterator(container, slots)) {
            ItemStack stack = container.getItem(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                count += stack.getCount();
                if (count >= minCount) return true;
            }
        }
        return false;
    }

    /**
     * Checks if a container's section contains at least {@code minCount} matching items.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param minCount Minimum required items (≥1)
     * @param section Inventory section to check
     * @return True if section contains sufficient items, false otherwise
     * @throws NullPointerException if any parameter is null
     */
    public static boolean hasItemCount(Container container, InventoryPredicate predicate, int minCount, InventorySection section) {
        Objects.requireNonNull(section, "Section cannot be null");
        return hasItemCount(container, predicate, minCount, section.getSlotRange());
    }

    /**
     * Checks if a container has at least one item matching the predicate.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param slots Slot range to check (null for entire inventory)
     * @return True if at least one matching item exists
     * @throws NullPointerException if container or predicate is null
     */
    public static boolean hasItem(Container container, InventoryPredicate predicate, @Nullable SlotRange slots) {
        return hasItemCount(container, predicate, 1, slots);
    }

    /**
     * Checks if a container's section contains at least one matching item.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param section Inventory section to check
     * @return True if section contains at least one matching item
     * @throws NullPointerException if any parameter is null
     */
    public static boolean hasItem(Container container, InventoryPredicate predicate, InventorySection section) {
        Objects.requireNonNull(section, "Section cannot be null");
        return hasItem(container, predicate, section.getSlotRange());
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
    public static int findFirstMatchingSlot(Container container, InventoryPredicate predicate, @Nullable SlotRange slots) {
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

    /**
     * Finds the first slot in a container section with a matching item.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param section Section to search within
     * @return Slot index of first match, or -1 if none
     * @throws NullPointerException if any parameter is null
     */
    public static int findFirstMatchingSlot(Container container, InventoryPredicate predicate, InventorySection section) {
        Objects.requireNonNull(section, "Section cannot be null");
        return findFirstMatchingSlot(container, predicate, section.getSlotRange());
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
    public static void removeItems(Container container, InventoryPredicate predicate, int amount,
                                   @Nullable SlotRange slots, InventoryCycleOrder order) {
        validateContainer(container);
        Objects.requireNonNull(predicate, "Predicate cannot be null");
        Objects.requireNonNull(order, "Removal order cannot be null");
        if (amount < 1) throw new IllegalArgumentException("Amount must be ≥1");

        List<Integer> slotOrder = order.getSlotOrder(container, slots);
        int remaining = amount;

        for (int slot : slotOrder) {
            ItemStack stack = container.getItem(slot);
            if (stack.isEmpty() || !predicate.test(stack)) continue;

            int remove = Math.min(stack.getCount(), remaining);
            stack.shrink(remove);
            remaining -= remove;

            if (stack.isEmpty()) {
                container.setItem(slot, ItemStack.EMPTY);
            }

            if (remaining <= 0) break;
        }
    }

    /**
     * Removes items from a specific container section.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param amount Maximum items to remove
     * @param section Section to remove from
     * @param order Slot processing order
     * @throws NullPointerException if any parameter is null
     */
    public static void removeItems(Container container, InventoryPredicate predicate, int amount, InventorySection section, InventoryCycleOrder order) {
        Objects.requireNonNull(section, "Section cannot be null");
        removeItems(container, predicate, amount, section.getSlotRange(), order);
    }

    /**
     * Removes a single item from the specified slot range.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param slots Slot range to search
     * @param order Slot processing order
     * @throws NullPointerException if any parameter is null
     */
    public static void removeSingleItem(Container container, InventoryPredicate predicate, SlotRange slots, InventoryCycleOrder order) {
        removeItems(container, predicate, 1, slots, order);
    }

    /**
     * Removes a single item from a container section.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param section Section to remove from
     * @param order Slot processing order
     * @throws NullPointerException if any parameter is null
     */
    public static void removeSingleItem(Container container, InventoryPredicate predicate, InventorySection section, InventoryCycleOrder order) {
        Objects.requireNonNull(section, "Section cannot be null");
        removeSingleItem(container, predicate, section.getSlotRange(), order);
    }

    /**
     * Attempts to add an item stack to a container.
     * @param container Target inventory
     * @param stack Item stack to add (will not be modified)
     * @return Remaining items that couldn't be added (empty stack if all were added)
     * @throws NullPointerException if container or stack is null
     */
    public static ItemStack addItem(Container container, ItemStack stack) {
        validateContainer(container);
        Objects.requireNonNull(stack, "ItemStack cannot be null");
        if (stack.isEmpty()) return ItemStack.EMPTY;

        ItemStack remaining = stack.copy();

        remaining = tryMergeWithExisting(container, remaining);
        if (remaining.isEmpty()) return ItemStack.EMPTY;

        remaining = tryFillEmptySlots(container, remaining);

        return remaining;
    }

    /**
     * Adds items to a container and permanently discards any overflow
     * @param container Target inventory
     * @param stack Item stack to add (will not be modified)
     * @throws NullPointerException if container or stack is null
     */
    public static void addAndDiscardOverflow(Container container, ItemStack stack) {
        addItem(container, stack);
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
    public static ImmutableList<ItemStack> collectMatching(Container container, InventoryPredicate predicate, @Nullable SlotRange slots) {
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
     * Collects copies of items from a container section matching the predicate.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param section Section to search
     * @return Immutable list of matching item copies
     * @throws NullPointerException if any parameter is null
     */
    public static ImmutableList<ItemStack> collectMatching(Container container, InventoryPredicate predicate, InventorySection section) {
        Objects.requireNonNull(section, "Section cannot be null");
        return collectMatching(container, predicate, section.getSlotRange());
    }

    /**
     * Calculates total available space for a specific item type.
     *
     * @param container Container to check
     * @param stack Item type to check space for
     * @return Total number of items that can be added
     * @throws NullPointerException if container or stack is null
     */
    public static int getAvailableSpace(Container container, ItemStack stack) {
        validateContainer(container);
        Objects.requireNonNull(stack, "ItemStack cannot be null");
        int space = 0;
        ItemStack testStack = stack.copy();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);
            if (existing.isEmpty()) {
                space += testStack.getMaxStackSize();
            } else if (ItemStack.isSameItem(existing, testStack)) {
                space += testStack.getMaxStackSize() - existing.getCount();
            }
        }
        return space;
    }

    // ======================== HELPER METHODS ======================== //

    private static Iterable<Integer> getSlotIterator(Container container, @Nullable SlotRange range) {
        return range != null ? range.getSlots(container) :
                () -> new SlotIterator(0, container.getContainerSize());
    }

    private static ItemStack tryMergeWithExisting(Container container, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);

            if (ItemStack.isSameItem(existing, remaining)) {
                int transfer = Math.min(remaining.getCount(), existing.getMaxStackSize() - existing.getCount());

                if (transfer > 0) {
                    existing.grow(transfer);
                    remaining.shrink(transfer);
                    container.setItem(slot, existing);

                    if (remaining.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                }
            }
        }

        return remaining;
    }

    private static ItemStack tryFillEmptySlots(Container container, ItemStack stack) {
        ItemStack remaining = stack.copy();

        for (int slot = 0; slot < container.getContainerSize(); slot++) {
            ItemStack existing = container.getItem(slot);

            if (existing.isEmpty()) {
                int transfer = Math.min(remaining.getCount(), remaining.getMaxStackSize());
                ItemStack newStack = remaining.copy();
                newStack.setCount(transfer);

                container.setItem(slot, newStack);
                remaining.shrink(transfer);

                if (remaining.isEmpty()) {
                    return ItemStack.EMPTY;
                }
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
        if (container instanceof Entity e) {
            level = e.level();
        } else if (container instanceof BlockEntity be) {
            level = be.getLevel();
        }

        if (!allowClientSide && level != null && level.isClientSide) {
            throw new IllegalStateException("Client-side inventory modifications are not allowed");
        }

        int size = container.getContainerSize();
        if (size <= 0) {
            throw new IllegalArgumentException("Container has invalid size: " + size);
        }

        // Validate slot access
        try {
            container.getItem(0);
            if (size > 1) container.getItem(size - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Container slot access violation", e);
        }
    }

    // Disable Client-side by default
    public static void validateContainer(Container container) {
        validateContainer(container, false);
    }
}