package net.xun.lib.common.api.item.tools;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.api.util.LazyReference;

import java.util.*;
import java.util.function.Supplier;

/**
 * Represents a complete set of tools (sword, axe, pickaxe, shovel, hoe) with configurable attributes.
 * Use the nested Builder class to configure and create tool sets with consistent properties.
 */
public class ToolSet {

    private final Map<ToolType, LazyReference<? extends Item>> tools = new EnumMap<>(ToolType.class);

    protected ToolSet(String name, Tier tier, EnumMap<ToolType, Float> attackDamage, EnumMap<ToolType, Float> attackSpeed, Item.Properties properties, ToolConfigurator configuration, AttributeHelper attributeHelper) {

        for (ToolType type : ToolType.values()) {
            String fullName = name + type.getNameSuffix();

            Item.Properties finalProperties = attributeHelper.applyAttributes(
                    properties, tier.getAttackDamageBonus() + attackDamage.get(type), attackSpeed.get(type)
            );

            tools.put(type, new LazyReference<>(
                    fullName,
                    () -> configuration.createTool(type, tier, finalProperties))
            );
        }
    }

    public Map<ResourceLocation, Supplier<? extends Item>> getItemsForRegistration() {
        Map<ResourceLocation, Supplier<? extends Item>> items = new LinkedHashMap<>();

        for (Map.Entry<ToolType, LazyReference<? extends Item>> entry : tools.entrySet()) {
            ResourceLocation id = CommonUtils.modLoc(entry.getValue().getName());
            items.put(id, entry.getValue());
        }

        return items;
    }

    /**
     * @return Supplier of the registered sword item
     */
    public Supplier<SwordItem> getSword() {
        return getTool(ToolType.SWORD);
    }

    /**
     * @return Supplier of the registered axe item
     */
    public Supplier<AxeItem> getAxe() {
        return getTool(ToolType.AXE);
    }

    /**
     * @return Supplier of the registered pickaxe item
     */
    public Supplier<PickaxeItem> getPickaxe() {
        return getTool(ToolType.PICKAXE);
    }

    /**
     * @return Supplier of the registered hoe item
     */
    public Supplier<HoeItem> getHoe() {
        return getTool(ToolType.HOE);
    }

    /**
     * @return Supplier of the registered shovel item
     */
    public Supplier<ShovelItem> getShovel() {
        return getTool(ToolType.SHOVEL);
    }

    @SuppressWarnings("unchecked")
    private <T extends Item> Supplier<T> getTool(ToolType type) {
        return (Supplier<T>) tools.get(type);
    }

    /**
     * @return List of all registered tool items in this set
     */
    public List<Item> getAll() {
        return tools.values().stream()
                .map(supplier -> (Item) supplier.get())
                .toList();
    }

    /**
     * Builder for constructing ToolSet instances with flexible configuration.
     * Allows setting per-tool attributes, item properties, and registration behavior.
     */
    public static class Builder {
        private final String name;
        private final Tier tier;
        private final EnumMap<ToolType, Float> attackDamage = new EnumMap<>(ToolType.class);
        private final EnumMap<ToolType, Float> attackSpeed = new EnumMap<>(ToolType.class);
        private Item.Properties properties = new Item.Properties();
        private ToolConfigurator configuration = ToolConfigurator.DEFAULT;
        private final AttributeHelper attributeHelper;

        /**
         * Constructs a new ToolSet builder.
         *
         * @param name Base name for tools (will be combined with tool-specific suffixes)
         * @param tier Material tier for all tools in the set
         * @param attributeHelper Helper for applying item attributes to tool properties
         */
        public Builder(String name, Tier tier, AttributeHelper attributeHelper) {
            this.name = name;
            this.tier = tier;
            this.attributeHelper = attributeHelper;
            initializeDefaultStats();
        }

        private void initializeDefaultStats() {
            Arrays.stream(ToolType.values()).forEach(type -> {
                attackDamage.put(type, 0f);
                attackSpeed.put(type, 0f);
            });
        }

        /**
         * Sets attack stats for all tools using arrays. Array order must match ToolType.values().
         *
         * @param damages Attack damage values (added to tier's base damage)
         * @param speeds  Attack speed values
         * @return This builder for chaining
         * @throws IllegalArgumentException If array lengths don't match ToolType count
         */
        public Builder withToolStats(float[] damages, float[] speeds) {
            validateArrayStats(damages, speeds);
            ToolType[] types = ToolType.values();
            for (int i = 0; i < types.length; i++) {
                attackDamage.put(types[i], damages[i]);
                attackSpeed.put(types[i], speeds[i]);
            }
            return this;
        }

        /**
         * Sets attack stats for a specific tool type.
         *
         * @param type   Tool type to configure
         * @param damage Attack damage (added to tier's base damage)
         * @param speed  Attack speed
         * @return This builder for chaining
         */
        public Builder withToolStats(ToolType type, float damage, float speed) {
            attackDamage.put(type, damage);
            attackSpeed.put(type, speed);
            return this;
        }

        /**
         * Configures tools with vanilla Minecraft balance values.
         * Uses standard damage bonuses and speeds from vanilla tool behavior.
         *
         * @return This builder for chaining
         */
        public Builder withVanillaBalance() {
            return withToolStats(
                    new float[] { 3, 5, 1, 1.5F, 1 },
                    new float[] { 1.6F, 0.9F, 1.2F, 1.0F, 1.0F }
            );
        }

        /**
         * Sets base item properties for all tools in the set.
         *
         * @param properties Item properties (durability, rarity, etc.)
         * @return This builder for chaining
         */
        public Builder withItemProperties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        /**
         * Sets custom tool creation configuration.
         *
         * @param configurator Tool creation strategy implementation
         * @return This builder for chaining
         */
        public Builder withConfiguration(ToolConfigurator configurator) {
            this.configuration = configurator;
            return this;
        }

        /**
         * Builds the configured ToolSet instance.
         *
         * @return New ToolSet with specified configuration
         */
        public ToolSet build() {
            return new ToolSet(this.name, this.tier, this.attackDamage, this.attackSpeed, this.properties, this.configuration, this.attributeHelper);
        }

        private void validateArrayStats(float[] damages, float[] speeds) {
            int expected = ToolType.values().length;
            if (damages.length != expected || speeds.length != expected) {
                throw new IllegalArgumentException("Invalid stats array lengths. Expected " + expected + " elements. Tool order: " + Arrays.toString(ToolType.values()));
            }
        }
    }
}