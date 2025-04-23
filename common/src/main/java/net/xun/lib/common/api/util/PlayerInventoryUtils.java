package net.xun.lib.common.api.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;
import net.xun.lib.common.api.inventory.InventoryCycleOrder;
import net.xun.lib.common.api.inventory.PlayerInventorySection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * Specific utilities for managing player inventory, disable client-side by default
 * <p>
 * Methods for operating player inventory including:
 * <ul>
 *   <li>Checking and managing items in player's hands</li>
 *   <li>Searching and removing items from specific inventory sections</li>
 *   <li>Swapping or clearing items in hands</li>
 * </ul>
 *
 * @see InventoryUtils General inventory utilities
 */
public class PlayerInventoryUtils {

    private PlayerInventoryUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    // ======================== CORE CHECKS ======================== //

    /**
     * Checks if either of the player's hands is empty.
     *
     * @param player Target player, not null
     * @return True if either hand has an empty stack
     * @throws NullPointerException if player is null
     */
    public static boolean hasEmptyHand(@NotNull Player player) {
        validatePlayer(player);
        return player.getMainHandItem().isEmpty() ||
                player.getOffhandItem().isEmpty();
    }

    /**
     * Checks if the specified hand is empty.
     *
     * @param player Target player, not null
     * @param hand The hand to check
     * @return True if specified hand has an empty stack
     * @throws NullPointerException if player is null
     */
    public static boolean hasEmptyHand(@NotNull Player player, InteractionHand hand) {
        return player.getItemInHand(hand).isEmpty();
    }

    /**
     * Checks if a container contains at least {@code minCount} items matching the predicate.
     *
     * @param player The target player
     * @param predicate Item matching logic
     * @param minCount Minimum required items (≥1)
     * @param section Inventory section to check
     * @return True if container contains sufficient matching items, false otherwise
     * @throws NullPointerException if container or predicate is null
     */
    public static boolean hasItemCount(Player player, InventoryPredicate predicate, int minCount, PlayerInventorySection section) {
        return InventoryUtils.hasItemCount(player.getInventory(), predicate, minCount, section.getSlotRange());
    }

    /**
     * Checks if a container's section contains at least one matching item.
     *
     * @param player Target player
     * @param predicate Item matching logic
     * @param section Inventory section to check
     * @return True if section contains at least one matching item
     * @throws NullPointerException if any parameter is null
     */
    public static boolean hasItem(Player player, InventoryPredicate predicate, PlayerInventorySection section) {
        return InventoryUtils.hasItem(player.getInventory(), predicate, section.getSlotRange());
    }

    // ======================== SLOT SEARCHING ======================== //

    /**
     * Finds the first slot in an inventory section with a matching item.
     *
     * @param player Target player
     * @param predicate Item matching logic
     * @param section Section to search within
     * @return Slot index of first match, or -1 if none
     * @throws NullPointerException if any parameter is null
     */
    public static int findFirstMatchingSlot(Player player, InventoryPredicate predicate, PlayerInventorySection section) {
        return InventoryUtils.findFirstMatchingSlot(player.getInventory(), predicate, section.getSlotRange());
    }

    /**
     * Get the item of the specific slot in the inventory
     *
     * @param player Target player
     * @param slotIndex The slot index
     * @return ItemStack in the specific slot of the inventory
     * @see net.xun.lib.common.api.inventory.slot.SlotGetter For easier slot getting
     */
    public static ItemStack getItemInSlot(Player player, int slotIndex) {
        return InventoryUtils.getItemInSlot(player.getInventory(), slotIndex);
    }

    // ======================== ITEM MANAGEMENT ======================== //

    /**
     * Swaps items between the player's main hand and offhand.
     *
     * @param player The player whose hands should be swapped, not null
     * @throws NullPointerException if player is null
     */
    public static void swapHands(@NotNull Player player) {
        validatePlayer(player);
        Inventory inventory = player.getInventory();

        ItemStack mainHandItem = player.getMainHandItem();
        ItemStack offhandItem = player.getOffhandItem();

        inventory.setItem(EquipmentSlot.MAINHAND.getIndex(), offhandItem);
        inventory.setItem(EquipmentSlot.OFFHAND.getIndex(), mainHandItem);
    }

    /**
     * Sets the item in the player's main hand.
     * <p>
     * Replaces any existing item in the main hand.
     *
     * @param player the target player, not null
     * @param item   the item to set, not null
     * @throws NullPointerException if player or item is null
     */
    public static void setItemInMainHand(@NotNull Player player, @NotNull ItemStack item) {
        validatePlayer(player);
        Objects.requireNonNull(item, "Item cannot be null");
        player.getInventory().setItem(EquipmentSlot.MAINHAND.getIndex(), item);
    }

    /**
     * Sets the item in the player's offhand.
     * <p>
     * Replaces any existing item in the offhand.
     *
     * @param player the target player, not null
     * @param item   the item to set, not null
     * @throws NullPointerException if player or item is null
     */
    public static void setItemInOffHand(@NotNull Player player, @NotNull ItemStack item) {
        validatePlayer(player);
        Objects.requireNonNull(item, "Item cannot be null");
        player.getInventory().setItem(EquipmentSlot.OFFHAND.getIndex(), item);
    }

    /**
     * Clears both the main hand and offhand of the player.
     *
     * @param player the target player, not null
     * @throws NullPointerException if player is null
     */
    public static void clearBothHands(@NotNull Player player) {
        validatePlayer(player);
        player.getInventory().setItem(EquipmentSlot.MAINHAND.getIndex(), ItemStack.EMPTY);
        player.getInventory().setItem(EquipmentSlot.OFFHAND.getIndex(), ItemStack.EMPTY);
    }

    /**
     * Attempts to add the item to the player's main hand or offhand, whichever is empty first.
     * <p>
     * If both hands are occupied, the item is not added.
     *
     * @param player the target player, not null
     * @param item   the item to add, not null
     * @throws NullPointerException if player or item is null
     */
    public static void addItemToHands(@NotNull Player player, @NotNull ItemStack item) {
        validatePlayer(player);
        Objects.requireNonNull(item, "Item cannot be null");

        if (hasEmptyHand(player, InteractionHand.MAIN_HAND)) {
            setItemInMainHand(player, item);
        } else if (hasEmptyHand(player, InteractionHand.OFF_HAND)) {
            setItemInOffHand(player, item);
        }
    }

    /**
     * Removes items from a specific inventory section.
     *
     * @param player Target player
     * @param predicate Item matching logic
     * @param amount Maximum items to remove
     * @param section Section to remove from
     * @param order Slot processing order
     * @throws NullPointerException if any parameter is null
     */
    public static void extractItems(Player player, InventoryPredicate predicate, int amount, PlayerInventorySection section, InventoryCycleOrder order) {
        InventoryUtils.extractItems(player.getInventory(), predicate, amount, section.getSlotRange(), order);
    }

    /**
     * Removes a single item from an inventory section.
     *
     * @param player Target player
     * @param predicate Item matching logic
     * @param section Section to remove from
     * @param order Slot processing order
     * @throws NullPointerException if any parameter is null
     */
    public static void extractSingleItem(Player player, InventoryPredicate predicate, PlayerInventorySection section, InventoryCycleOrder order) {
        InventoryUtils.extractSingleItem(player.getInventory(), predicate, section.getSlotRange(), order);
    }

    /**
     * Attempts to add an item stack to an inventory.
     * @param player Target player
     * @param stack Item stack to add (will not be modified)
     * @return Remaining items that couldn't be added (empty stack if all were added)
     * @throws NullPointerException if container or stack is null
     */
    public static ItemStack insertItem(Player player, ItemStack stack) {
        return InventoryUtils.insertItem(player.getInventory(), stack);
    }

    /**
     * Adds items to a player's inventory and permanently discards any overflow
     * @param player Target player
     * @param stack Item stack to add (will not be modified)
     * @throws NullPointerException if container or stack is null
     */
    public static void insertAndDiscardOverflow(Player player, ItemStack stack) {
        InventoryUtils.insertAndDiscardOverflow(player.getInventory(), stack);
    }

    // ======================== UTILITY METHODS ======================== //

    /**
     * Collects copies of items from an inventory section matching the predicate.
     *
     * @param container Target inventory
     * @param predicate Item matching logic
     * @param section Section to search
     * @return Immutable list of matching item copies
     * @throws NullPointerException if any parameter is null
     */
    public static ImmutableList<ItemStack> collectMatching(Container container, InventoryPredicate predicate, PlayerInventorySection section) {
        Objects.requireNonNull(section, "Section cannot be null");
        return InventoryUtils.collectMatching(container, predicate, section.getSlotRange());
    }

    /**
     * Calculates total available space for a specific item type.
     *
     * @param player Target player
     * @return Total number of items that can be added
     * @throws NullPointerException if container or stack is null
     */
    public static int getAvailableSpace(Player player) {
        return InventoryUtils.getAvailableSpace(player.getInventory());
    }

    // ======================== HELPER METHODS ======================== //

    private static void validatePlayer(@Nullable Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        InventoryUtils.validateContainer(player.getInventory());
    }
}
