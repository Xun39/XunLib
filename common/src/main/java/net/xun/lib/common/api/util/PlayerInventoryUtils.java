package net.xun.lib.common.api.util;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;
import net.xun.lib.common.api.inventory.slot.InventoryCycleOrder;
import net.xun.lib.common.api.inventory.slot.InventorySection;
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

    public static boolean hasEmptyHand(@NotNull Player player, InteractionHand hand) {
        return player.getItemInHand(hand).isEmpty();
    }

    public static boolean hasItemCount(Player player, InventoryPredicate predicate, int minCount, InventorySection section) {
        return InventoryUtils.hasItemCount(player.getInventory(), predicate, minCount, section);
    }

    public static boolean hasItem(Player player, InventoryPredicate predicate, InventorySection section) {
        return InventoryUtils.hasItem(player.getInventory(), predicate, section);
    }

    // ======================== SLOT SEARCHING ======================== //

    public static void findFirstMatchingSlot(Player player, InventoryPredicate predicate, InventorySection section) {
        InventoryUtils.findFirstMatchingSlot(player.getInventory(), predicate, section);
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

    public static void extractItems(Player player, InventoryPredicate predicate, int amount, InventorySection section, InventoryCycleOrder order) {
        InventoryUtils.extractItems(player.getInventory(), predicate, amount, section, order);
    }

    public static void extractSingleItem(Player player, InventoryPredicate predicate, InventorySection section, InventoryCycleOrder order) {
        InventoryUtils.extractSingleItem(player.getInventory(), predicate, section, order);
    }

    public static void insertItem(Player player, ItemStack stack) {
        InventoryUtils.insetItem(player.getInventory(), stack);
    }

    // ======================== HELPER METHODS ======================== //

    private static void validatePlayer(@Nullable Player player) {
        Objects.requireNonNull(player, "Player cannot be null");
        InventoryUtils.validateContainer(player.getInventory());
    }
}
