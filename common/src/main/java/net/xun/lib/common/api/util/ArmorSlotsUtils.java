package net.xun.lib.common.api.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.xun.lib.common.api.inventory.predicates.InventoryPredicate;
import net.xun.lib.common.api.inventory.slot.SlotGetter;

import java.util.Arrays;
import java.util.Collection;

/**
 * Provides specialized utilities for managing and inspecting player armor equipment.
 * <p>
 * Utilities for armor slots with features including:
 * <ul>
 *   <li>Armor presence and completeness checks</li>
 *   <li>Material type verification and set validation</li>
 *   <li>Enchantment analysis and level summation</li>
 *   <li>Durability monitoring and repair needs detection</li>
 *   <li>Slot-specific operations and armor retrieval</li>
 * </ul>
 *
 *
 * @see InventoryUtils General inventory utilities
 * @see SlotGetter Armor Slot Indices
 * @see ArmorItem Armor item handling
 */
public class ArmorSlotsUtils {

    private static final int[] ALL_ARMOR_SLOTS = {
            EquipmentSlot.HEAD.getIndex(),
            EquipmentSlot.CHEST.getIndex(),
            EquipmentSlot.LEGS.getIndex(),
            EquipmentSlot.FEET.getIndex()
    };

    private ArmorSlotsUtils() {
        throw new AssertionError("Cannot instantiate utility class");
    }

    /**
     * Checks if a specific armor slot contains any armor item.
     *
     * @param player The target player
     * @param slotIndex Armor slot to check (see {@link SlotGetter})
     * @return True if the slot contains non-air armor, false otherwise
     * @throws NullPointerException if player is null
     */
    public static boolean hasArmorInSlot(Player player, int slotIndex) {
        return !getArmorInSlot(player, slotIndex).isEmpty();
    }

    public static boolean hasFullArmorSet(Player player) {
        return Arrays.stream(ALL_ARMOR_SLOTS).allMatch(slot -> hasArmorInSlot(player, slot));
    }

    /**
     * Checks if armor in a specific slot matches given material.
     *
     * @param player The target player
     * @param slotIndex Armor slot to check (see {@link SlotGetter})
     * @param material Required armor material
     * @return True if slot contains armor of specified material, false otherwise
     * @throws NullPointerException if any parameter is null
     */
    public static boolean isArmorMaterialInSlot(Player player, int slotIndex, Holder<ArmorMaterial> material) {
        ItemStack armor = getArmorInSlot(player, slotIndex);
        return armor.getItem() instanceof ArmorItem item && item.getMaterial() == material;
    }

    public static boolean hasFullArmorSetOfMaterial(Player player, Holder<ArmorMaterial> material) {
        return Arrays.stream(ALL_ARMOR_SLOTS).allMatch(slot -> isArmorMaterialInSlot(player, slot, material));
    }

    public static boolean hasEnchantmentInSlot(Player player, int slotIndex, Holder<Enchantment> enchantment) {
        ItemStack armor = getArmorInSlot(player, slotIndex);
        return !armor.getEnchantments().isEmpty();
    }

    public static int getTotalEnchantmentLevel(Player player, Holder<Enchantment> enchantment) {
        return Arrays.stream(ALL_ARMOR_SLOTS)
                .map(slot -> getArmorInSlot(player, slot).getEnchantments().getLevel(enchantment))
                .sum();
    }

    public static float getArmorDurabilityPercentage(int slotIndex, Player player) {
        ItemStack armor = getArmorInSlot(player, slotIndex);
        if (armor.isEmpty()) return 0;
        return (float) (armor.getMaxDamage() - armor.getDamageValue()) / armor.getMaxDamage();
    }

    public static boolean isArmorDamaged(int slotIndex, Player player) {
        ItemStack armor = getArmorInSlot(player, slotIndex);
        return !armor.isEmpty() && armor.getDamageValue() > 0;
    }

    public static boolean matchesInSlot(Player player, int slotIndex, InventoryPredicate condition) {
        return condition.test(getArmorInSlot(player, slotIndex));
    }

    public static boolean matchesAllArmor(Player player, InventoryPredicate condition) {
        return Arrays.stream(ALL_ARMOR_SLOTS)
                .allMatch(slot -> matchesInSlot(player, slot, condition));
    }

    public static ItemStack getArmorInSlot(Player player, int slotIndex) {
        return player.getInventory().getArmor(slotIndex);
    }

    public static Collection<ItemStack> getAllWornArmor(Player player) {
        return Arrays.stream(ALL_ARMOR_SLOTS)
                .mapToObj(slot -> getArmorInSlot(player, slot))
                .filter(stack -> !stack.isEmpty())
                .toList();
    }

    public static int countArmorPiecesOfMaterial(Player player, Holder<ArmorMaterial> material) {
        return (int) Arrays.stream(ALL_ARMOR_SLOTS)
                .filter(slot -> isArmorMaterialInSlot(player, slot, material))
                .count();
    }

    // Armor set bonus simulation
    public static boolean hasSetBonus(Player player, Holder<ArmorMaterial> material, int requiredPieces) {
        return countArmorPiecesOfMaterial(player, material) >= requiredPieces;
    }

    // Repair utilities
    public static boolean needsRepair(int slotIndex, Player player) {
        ItemStack armor = getArmorInSlot(player, slotIndex);
        return armor.isDamaged();
    }

    public static int countDamagedPieces(Player player) {
        return (int) Arrays.stream(ALL_ARMOR_SLOTS)
                .filter(slot -> isArmorDamaged(slot, player))
                .count();
    }
}
