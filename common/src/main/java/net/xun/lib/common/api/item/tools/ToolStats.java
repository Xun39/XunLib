package net.xun.lib.common.api.item.tools;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Container for attack damage and attack speed values for a tool piece.
 * <p>
 * These stats are combined with the {@link Tier} base damage to compute the final
 * attack damage attribute. The attack speed is stored as the actual value (e.g., 1.6)
 * and is adjusted during attribute application (subtracted by 4 to match Minecraft's
 * internal base speed).
 * </p>
 * <p>
 * This record also provides default constants for vanilla and common modded tool types.
 * </p>
 *
 * @param attackDamage the base attack damage added to the tier's bonus
 * @param attackSpeed  the attack speed value (e.g., 1.6 for swords)
 * @since 3.0.0
 */
public record ToolStats(float attackDamage, float attackSpeed) {
    public static final ToolStats ZERO = new ToolStats(0.0F, 0.0F);

    // Vanilla
    public static final ToolStats DEFAULT_SWORD = new ToolStats(3.0F, 1.6F);
    public static final ToolStats DEFAULT_AXE = new ToolStats(6.0F, 0.9F);
    public static final ToolStats DEFAULT_PICKAXE = new ToolStats(1.0F, 1.2F);
    public static final ToolStats DEFAULT_SHOVEL = new ToolStats(1.5F, 1.0F);
    public static final ToolStats DEFAULT_HOE = new ToolStats(-2.0F, 3.0F);

    // Modded
    // Knife from Farmer's Delight
    public static final ToolStats DEFAULT_KNIFE = new ToolStats(0.5F, 2.0F);

    /**
     * Applies the attack damage and speed attributes to the given builder,
     * using the provided tier to compute the final damage bonus.
     *
     * @param tier    the material tier (provides base attack damage bonus)
     * @param builder the attribute builder to modify
     */
    public void addBaseAttributes(Tier tier, ItemAttributeModifiers.Builder builder) {
        builder.add(
                Attributes.ATTACK_DAMAGE,
                new AttributeModifier(
                        Item.BASE_ATTACK_DAMAGE_ID,
                        attackDamage() + tier.getAttackDamageBonus(),
                        AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND
        );
        builder.add(
                Attributes.ATTACK_SPEED,
                new AttributeModifier(
                        Item.BASE_ATTACK_SPEED_ID,
                        attackSpeed() - 4,
                        AttributeModifier.Operation.ADD_VALUE),
                EquipmentSlotGroup.MAINHAND
        );
    }
}
