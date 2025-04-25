package net.xun.lib.common.api.item.tools;

import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

/**
 * Default implementation applying standard Minecraft attack attributes.
 */
public class GenericAttributeHelper implements AttributeHelper {

    /**
     * Applies standard combat attributes using Minecraft's attribute system.
     *
     * @param properties Initial item properties to modify
     * @param damage Total attack damage (tier base + bonus)
     * @param speed Attack speed (this is the actual attack speed)
     * @return Modified properties with attack attributes
     */
    @Override
    public Item.Properties applyAttributes(Item.Properties properties, float damage, float speed) {
        return properties.attributes(createAttributeModifiers(damage, speed));
    }

    /**
     * Creates attribute modifiers following vanilla Minecraft conventions.
     *
     * @param damage Final attack damage value
     * @param speed Attack speed offset from base 4.0
     * @return Configured attribute modifiers container
     */
    private static ItemAttributeModifiers createAttributeModifiers(float damage, float speed) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                Item.BASE_ATTACK_DAMAGE_ID,
                                damage,
                                AttributeModifier.Operation.ADD_VALUE
                ), EquipmentSlotGroup.MAINHAND)
                .add(Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                Item.BASE_ATTACK_SPEED_ID,
                                speed - 4,
                                AttributeModifier.Operation.ADD_VALUE
                ),EquipmentSlotGroup.MAINHAND)
                .build();
    }
}
