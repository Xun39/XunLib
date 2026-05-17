package net.xun.lib.common.api.util;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.xun.lib.common.api.exceptions.UtilityClassException;
import net.xun.lib.common.api.inventory.ItemStackPredicate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * Provides specialized utilities for managing and inspecting equipment worn by any
 * {@link LivingEntity}, including players, mobs, and armor stands.
 * <p>
 * This class replaces the player-specific {@code PlayerArmorSlotsUtils} and supports
 * all entities that can equip items in standard armor slots (HEAD, CHEST, LEGS, FEET).
 * <p>
 * Features include:
 * <ul>
 *   <li>Checking presence of equipment in individual slots or full sets</li>
 *   <li>Verifying armor material types and checking for full material sets</li>
 *   <li>Applying custom {@link ItemStackPredicate} conditions to equipment</li>
 *   <li>Retrieving all worn armor as a list</li>
 *   <li>Counting pieces of a specific material</li>
 * </ul>
 *
 * @see LivingEntity
 * @see EquipmentSlot
 * @see ArmorItem
 * @see ItemStackPredicate
 */
public final class EquipmentSlotsUtils {
    private EquipmentSlotsUtils() throws UtilityClassException {
        throw new UtilityClassException();
    }

    /**
     * Checks whether the specified equipment slot of the entity contains a non-empty item stack.
     *
     * @param entity the living entity to check (must not be null)
     * @param slot   the equipment slot to inspect (must not be null)
     * @return true if the slot contains an item, false if the stack is empty
     * @throws NullPointerException if either parameter is null
     */
    public static boolean hasEquipmentInSlot(LivingEntity entity, EquipmentSlot slot) {
        return !getEquipmentInSlot(entity, slot).isEmpty();
    }

    /**
     * Checks whether the entity is wearing a full set of armor, i.e., all four humanoid armor slots
     * (HEAD, CHEST, LEGS, FEET) contain a non-empty item stack.
     *
     * @param entity the living entity to check (must not be null)
     * @return true if all four armor slots are occupied, false otherwise
     * @throws NullPointerException if entity is null
     */
    public static boolean hasFullArmorSet(LivingEntity entity) {
        return getHumanoidArmorSlots().stream().allMatch(slot -> hasEquipmentInSlot(entity, slot));
    }

    /**
     * Verifies whether the item in the specified equipment slot is an armor piece made of the given material.
     *
     * @param entity   the living entity (must not be null)
     * @param slot     the equipment slot to check (must be an armor slot, otherwise always returns false)
     * @param material the required {@link ArmorMaterial} (must not be null)
     * @return true if the slot contains an {@link ArmorItem} with exactly the specified material,
     *         otherwise false
     * @throws NullPointerException if any argument is null
     */
    public static boolean isArmorMaterialInSlot(LivingEntity entity, EquipmentSlot slot, Holder<ArmorMaterial> material) {
        ItemStack armor = getEquipmentInSlot(entity, slot);
        return armor.getItem() instanceof ArmorItem item && item.getMaterial() == material;
    }

    /**
     * Checks whether the entity wears a full armor set where every piece is made of the specified material.
     * <p>
     * This method checks all four humanoid armor slots; if any slot is empty or contains an item that is
     * not an armor piece of the required material, the result is false.
     *
     * @param entity   the living entity (must not be null)
     * @param material the required armor material (must not be null)
     * @return true if all four armor slots contain an {@link ArmorItem} of the given material,
     *         otherwise false
     * @throws NullPointerException if any argument is null
     */
    public static boolean hasFullSetOfMaterial(LivingEntity entity, Holder<ArmorMaterial> material) {
        return getHumanoidArmorSlots().stream().allMatch(slot -> isArmorMaterialInSlot(entity, slot, material));
    }

    /**
     * Tests whether the item in the specified equipment slot satisfies the given predicate.
     *
     * @param entity    the living entity (must not be null)
     * @param slot      the equipment slot to test (must not be null)
     * @param predicate the condition to apply to the item stack (must not be null)
     * @return true if the predicate's test method returns true for the item stack,
     *         otherwise false
     * @throws NullPointerException if any argument is null
     */
    public static boolean matchesInSlot(LivingEntity entity, EquipmentSlot slot, ItemStackPredicate predicate) {
        return predicate.test(getEquipmentInSlot(entity, slot));
    }

    /**
     * Checks whether all armor slots (HEAD, CHEST, LEGS, FEET) contain items that satisfy the given predicate.
     *
     * @param entity    the living entity (must not be null)
     * @param predicate the condition to apply to each armor piece (must not be null)
     * @return true if the predicate holds for every non‑empty armor slot, false otherwise
     * @throws NullPointerException if any argument is null
     */
    public static boolean matchesAllArmor(LivingEntity entity, ItemStackPredicate predicate) {
        return getHumanoidArmorSlots().stream().allMatch(slot -> matchesInSlot(entity, slot, predicate));
    }

    /**
     * Retrieves the item stack currently equipped in the specified slot of the entity.
     * <p>
     * This is a direct delegate to {@link LivingEntity#getItemBySlot(EquipmentSlot)}.
     *
     * @param entity the living entity (must not be null)
     * @param slot   the equipment slot (must not be null)
     * @return the item stack in that slot (may be empty, never null)
     * @throws NullPointerException if either argument is null
     */
    public static ItemStack getEquipmentInSlot(LivingEntity entity, EquipmentSlot slot) {
        return entity.getItemBySlot(slot);
    }

    /**
     * Returns a list of all non‑empty armor stacks currently worn by the entity.
     * <p>
     * The order corresponds to the natural iteration order of {@link LivingEntity#getArmorSlots()},
     * which typically starts from the feet slot (FEET) and goes up to the head slot (HEAD).
     *
     * @param entity the living entity (must not be null)
     * @return an immutable list of worn armor stacks (empty list if none)
     * @throws NullPointerException if entity is null
     */
    public static List<ItemStack> getAllWornArmor(LivingEntity entity) {
        return StreamSupport.stream(entity.getArmorSlots().spliterator(), false).toList();
    }

    /**
     * Counts how many of the entity's armor slots contain an armor piece of the specified material.
     *
     * @param entity   the living entity (must not be null)
     * @param material the required armor material (must not be null)
     * @return the number of occupied armor slots that hold an {@link ArmorItem} with the given material
     *         (values 0–4)
     * @throws NullPointerException if any argument is null
     */
    public static int getMaterialPiecesCount(LivingEntity entity, Holder<ArmorMaterial> material) {
        return (int) Arrays.stream(EquipmentSlot.values())
                .filter(slot ->
                        slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR
                                && isArmorMaterialInSlot(entity, slot, material)
                )
                .count();
    }

    /**
     * Returns a list of all equipment slots that belong to the humanoid armor type.
     * <p>
     * The returned list contains the following slots (in the order defined by
     * {@link EquipmentSlot#values()}): HEAD, CHEST, LEGS, FEET. This list is immutable.
     * <p>
     * This method is useful for iterating over all armor slots without hard‑coding the filter.
     *
     * @return an immutable list of {@link EquipmentSlot} instances where
     *         {@link EquipmentSlot#getType()} equals {@link EquipmentSlot.Type#HUMANOID_ARMOR}
     */
    public static List<EquipmentSlot> getHumanoidArmorSlots() {
        return Arrays.stream(EquipmentSlot.values())
                .filter(slot -> slot.getType() == EquipmentSlot.Type.HUMANOID_ARMOR)
                .toList();
    }
}
