package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.*;

/**
 * Enumerates tool types with their registration suffixes and factory methods.
 */
public enum ToolType {

    /** Sword tool type */
    SWORD("_sword", SwordItem::new),
    /** Axe tool type */
    AXE("_axe", AxeItem::new),
    /** Pickaxe tool type */
    PICKAXE("_pickaxe", PickaxeItem::new),
    /** Hoe tool type */
    HOE("_hoe", HoeItem::new),
    /** Shovel tool type */
    SHOVEL("_shovel", ShovelItem::new);

    /** Suffix appended to base name for registration */
    public final String nameSuffix;
    private final ToolFactory factory;

    /**
     * @param registrationSuffix Suffix for registry name
     * @param factory Tool-specific constructor
     */
    ToolType(String registrationSuffix, ToolFactory factory) {
        this.nameSuffix = registrationSuffix;
        this.factory = factory;
    }

    /**
     * Creates a tool item instance.
     *
     * @param tier Material tier for tool durability
     * @param properties Base item properties
     * @return Configured tool item
     */
    Item create(Tier tier, Item.Properties properties) {
        return factory.create(tier, properties);
    }

    /**
     * Functional interface for tool item construction.
     */
    @FunctionalInterface
    interface ToolFactory {
        /**
         * Creates a tool item instance.
         *
         * @param tier Material tier
         * @param props Item properties
         * @return New tool item
         */
        Item create(Tier tier, Item.Properties props);
    }
}