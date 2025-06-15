package net.xun.lib.common.api.item.tools;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.api.util.LazyReference;

import java.util.*;
import java.util.function.Supplier;

/**
 * Represents a complete set of tools (sword, axe, pickaxe, shovel, hoe) with configurable attributes.
 * Use the nested {@link Builder} to configure and create tool sets with consistent properties.
 */
public class ToolSet {

    private final String name;
    private final Map<ToolType, LazyReference<? extends Item>> tools = new EnumMap<>(ToolType.class);

    protected ToolSet(String name,
                      Tier tier,
                      EnumMap<ToolType, Float> attackDamage,
                      EnumMap<ToolType, Float> attackSpeed,
                      Supplier<Item.Properties> propertiesSupplier,
                      ToolConfigurator configuration,
                      AttributeHelper attributeHelper) {

        for (ToolType type : ToolType.values()) {
            String fullName = name + type.getNameSuffix();

            Item.Properties toolProperties = propertiesSupplier.get();

            Item.Properties finalProperties = attributeHelper.applyAttributes(
                    toolProperties,
                    tier.getAttackDamageBonus() + attackDamage.get(type),
                    attackSpeed.get(type)
            );

            tools.put(type, new LazyReference<>(fullName,
                    () -> configuration.createTool(type, tier, finalProperties))
            );
        }
        this.name = name;
    }

    /**
     * Retrieves all tool items in this set for registration purposes.
     *
     * @return Map of {@link ResourceLocation} keys to {@link Supplier} providers for tool items
     */
    public Map<ResourceLocation, Supplier<? extends Item>> getItemsForRegistration() {
        Map<ResourceLocation, Supplier<? extends Item>> items = new LinkedHashMap<>();

        for (Map.Entry<ToolType, LazyReference<? extends Item>> entry : tools.entrySet()) {
            ResourceLocation id = CommonUtils.modLoc(entry.getValue().getName());
            items.put(id, entry.getValue());
        }

        return items;
    }

    /**
     * Gets the base name of this tool set.
     *
     * @return The base name of this tool set
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the sword item from this tool set.
     *
     * @return Supplier providing the registered {@link SwordItem}
     */
    public Supplier<SwordItem> getSword() {
        return getTool(ToolType.SWORD);
    }

    /**
     * Gets the axe item from this tool set.
     *
     * @return Supplier providing the registered {@link AxeItem}
     */
    public Supplier<AxeItem> getAxe() {
        return getTool(ToolType.AXE);
    }

    /**
     * Gets the pickaxe item from this tool set.
     *
     * @return Supplier providing the registered {@link PickaxeItem}
     */
    public Supplier<PickaxeItem> getPickaxe() {
        return getTool(ToolType.PICKAXE);
    }

    /**
     * Gets the hoe item from this tool set.
     *
     * @return Supplier providing the registered {@link HoeItem}
     */
    public Supplier<HoeItem> getHoe() {
        return getTool(ToolType.HOE);
    }

    /**
     * Gets the shovel item from this tool set.
     *
     * @return Supplier providing the registered {@link ShovelItem}
     */
    public Supplier<ShovelItem> getShovel() {
        return getTool(ToolType.SHOVEL);
    }

    @SuppressWarnings("unchecked")
    private <T extends Item> Supplier<T> getTool(ToolType type) {
        return (Supplier<T>) tools.get(type);
    }

    /**
     * Retrieves all registered tool items in this set.
     *
     * @return List containing all tool items
     */
    public List<Item> getAll() {
        return tools.values().stream()
                .map(supplier -> (Item) supplier.get())
                .toList();
    }

    /**
     * Builder for constructing {@link ToolSet} instances with flexible configuration.
     * Allows setting per-tool attributes, item properties, and creation behavior.
     */
    public static class Builder {
        private final String name;
        private final Tier tier;
        private final EnumMap<ToolType, Float> attackDamage = new EnumMap<>(ToolType.class);
        private final EnumMap<ToolType, Float> attackSpeed = new EnumMap<>(ToolType.class);
        private Supplier<Item.Properties> propertiesSupplier = Item.Properties::new;
        private ToolConfigurator configuration = ToolConfigurator.DEFAULT;
        private final AttributeHelper attributeHelper;

        /**
         * Constructs a new builder for a tool set.
         *
         * @param name Base name for tools (appended with tool-specific suffixes)
         * @param tier Material tier for all tools
         * @param attributeHelper Helper for applying item attributes to properties
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
         * Configures attack stats for all tools using arrays.
         * Array order must match {@link ToolType#values()}.
         *
         * @param damages Attack damage bonuses (added to tier's base damage)
         * @param speeds  Attack speed modifiers
         * @return This builder for chaining
         * @throws IllegalArgumentException If array lengths don't match tool types
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
         * Configures attack stats for a specific tool type.
         *
         * @param type   Target tool type
         * @param damage Attack damage bonus (added to tier's base damage)
         * @param speed  Attack speed modifier
         * @return This builder for chaining
         */
        public Builder withToolStats(ToolType type, float damage, float speed) {
            attackDamage.put(type, damage);
            attackSpeed.put(type, speed);
            return this;
        }

        /**
         * Applies vanilla Minecraft balance values to all tools. (iron tools stats)
         * Uses standard damage bonuses and attack speeds from vanilla.
         *
         * @return This builder for chaining
         */
        public Builder withVanillaBalance() {
            return withToolStats(
                    new float[] { 3, 6, 1, -2.0F, 1.5F },
                    new float[] { 1.6F, 0.9F, 1.2F, 3.0F, 1.0F }
            );
        }

        /**
         * <b>Caution:</b> Sets shared item properties for all tools.
         * May cause attribute conflicts if properties are mutated internally.
         * Prefer {@link #withItemPropertiesSupplier} for multi-tool sets.
         *
         * @param properties Base properties for all tools
         * @return This builder for chaining
         */
        public Builder withItemProperties(Item.Properties properties) {
            this.propertiesSupplier = () -> properties;
            return this;
        }

        /**
         * Sets item properties using a supplier (called per-tool during construction).
         * Safer than {@link #withItemProperties} for tool sets.
         *
         * @param propertiesSupplier Supplier providing base properties
         * @return This builder for chaining
         */
        public Builder withItemPropertiesSupplier(Supplier<Item.Properties> propertiesSupplier) {
            this.propertiesSupplier = propertiesSupplier;
            return this;
        }

        /**
         * Sets custom tool creation logic.
         *
         * @param configurator Tool creation strategy implementation
         * @return This builder for chaining
         */
        public Builder withConfiguration(ToolConfigurator configurator) {
            this.configuration = configurator;
            return this;
        }

        /**
         * Constructs the configured {@link ToolSet}.
         *
         * @return New tool set instance
         */
        public ToolSet build() {
            return new ToolSet(this.name, this.tier, this.attackDamage, this.attackSpeed, this.propertiesSupplier, this.configuration, this.attributeHelper);
        }

        private void validateArrayStats(float[] damages, float[] speeds) {
            int expected = ToolType.values().length;
            if (damages.length != expected || speeds.length != expected) {
                throw new IllegalArgumentException("Invalid stats array lengths. Expected " + expected + " elements. Tool order: " + Arrays.toString(ToolType.values()));
            }
        }
    }
}