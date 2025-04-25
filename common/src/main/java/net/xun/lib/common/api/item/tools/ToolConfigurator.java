package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/**
 * Factory interface for creating custom tool items.
 */
public interface ToolConfigurator {

    /**
     * Default configuration using standard tool constructors.
     */
    ToolConfigurator DEFAULT = ToolType::create;

    /**
     * Creates a tool item instance.
     *
     * @param type Type of tool to create
     * @param tier Material tier
     * @param properties Item properties stack
     * @return Configured tool item
     */
    Item createTool(ToolType type, Tier tier, Item.Properties properties);
}
