package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;

/**
 * Defines a strategy for applying attribute modifiers to tool items.
 */
public interface AttributeHelper {

    /**
     * Applies combat attributes to tool properties.
     *
     * @param properties Initial item properties to enhance
     * @param damage Attack damage
     * @param speed Attack speed
     * @return Enhanced item properties with combat attributes
     */
    Item.Properties applyAttributes(Item.Properties properties, float damage, float speed);
}
