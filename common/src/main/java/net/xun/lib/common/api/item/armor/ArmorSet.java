package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.util.LazyReference;
import net.xun.lib.common.api.util.CommonUtils;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Represents a complete set of armor (helmet, chestplate, leggings, boots).
 * Use the nested {@link Builder} to configure armor pieces with consistent properties.
 */
public class ArmorSet {

    private final Map<ArmorType, LazyReference<ArmorItem>> armors = new EnumMap<>(ArmorType.class);

    protected ArmorSet(String name,
                       Holder<ArmorMaterial> material,
                       int durabilityFactor,
                       Supplier<Item.Properties> propertiesSupplier,
                       ArmorConfigurator configuration) {

        for (ArmorType type : ArmorType.values()) {
            String fullName = name + type.getNameSuffix();

            Item.Properties armorProperties = propertiesSupplier.get();

            armors.put(type, new LazyReference<>(
                    fullName,
                    () -> configuration.createArmor(type, material, durabilityFactor, armorProperties))
            );
        }
    }

    /**
     * Retrieves all armor items in this set for registration purposes.
     *
     * @return Map of {@link ResourceLocation} keys to {@link Supplier} providers for armor items
     */
    public Map<ResourceLocation, Supplier<ArmorItem>> getItemsForRegistration() {
        Map<ResourceLocation, Supplier<ArmorItem>> items = new LinkedHashMap<>();

        for (Map.Entry<ArmorType, LazyReference<ArmorItem>> entry : armors.entrySet()) {
            ResourceLocation id = CommonUtils.modLoc(entry.getValue().getName());
            items.put(id, entry.getValue());
        }

        return items;
    }

    /**
     * Gets the helmet item from this armor set.
     *
     * @return Supplier providing the registered helmet
     */
    public Supplier<ArmorItem> getHelmet() {
        return armors.get(ArmorType.HELMET);
    }

    /**
     * Gets the chestplate item from this armor set.
     *
     * @return Supplier providing the registered chestplate
     */
    public Supplier<ArmorItem> getChestplate() {
        return armors.get(ArmorType.CHESTPLATE);
    }

    /**
     * Gets the leggings item from this armor set.
     *
     * @return Supplier providing the registered leggings
     */
    public Supplier<ArmorItem> getLeggings() {
        return armors.get(ArmorType.LEGGINGS);
    }

    /**
     * Gets the boots item from this armor set.
     *
     * @return Supplier providing the registered boots
     */
    public Supplier<ArmorItem> getBoots() {
        return armors.get(ArmorType.BOOTS);
    }

    /**
     * Retrieves all registered armor items in this set.
     *
     * @return List containing all armor items
     */
    public List<Item> getAll() {
        return armors.values().stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }

    /**
     * Builder for constructing {@link ArmorSet} instances.
     */
    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private int durabilityFactor;
        private Supplier<Item.Properties> propertiesSupplier = Item.Properties::new;
        private ArmorConfigurator configuration = ArmorConfigurator.DEFAULT;

        /**
         * Constructs a new builder for an armor set.
         *
         * @param name Base name for armor pieces (appended with armor-specific suffixes)
         * @param material Armor material holder
         */
        public Builder(String name, Holder<ArmorMaterial> material) {
            this.name = name;
            this.material = material;
        }

        /**
         * Sets durability multiplier for all armor pieces.
         * Final durability = material durability * factor.
         *
         * @param durabilityFactor Multiplier for base material durability
         * @return This builder for chaining
         */
        public Builder withDurabilityFactor(int durabilityFactor) {
            this.durabilityFactor = durabilityFactor;
            return this;
        }

        /**
         * <b>Caution:</b> Sets shared item properties for all armor pieces.
         * May cause issues if properties are mutated internally.
         * Prefer {@link #withItemPropertiesSupplier} for multi-piece sets.
         *
         * @param properties Base properties for all armor
         * @return This builder for chaining
         */
        public Builder withItemProperties(Item.Properties properties) {
            this.propertiesSupplier = () -> properties;
            return this;
        }

        /**
         * Sets item properties using a supplier (called per-piece during construction).
         * Safer than {@link #withItemProperties} for armor sets.
         *
         * @param propertiesSupplier Supplier providing base properties
         * @return This builder for chaining
         */
        public Builder withItemPropertiesSupplier(Supplier<Item.Properties> propertiesSupplier) {
            this.propertiesSupplier = propertiesSupplier;
            return this;
        }

        /**
         * Sets custom armor creation logic.
         *
         * @param configuration Armor creation strategy implementation
         * @return This builder for chaining
         */
        public Builder withConfiguration(ArmorConfigurator configuration) {
            this.configuration = configuration;
            return this;
        }

        /**
         * Constructs the configured {@link ArmorSet}.
         *
         * @return New armor set instance
         */
        public ArmorSet build() {
            return new ArmorSet(this.name, this.material, this.durabilityFactor, this.propertiesSupplier, this.configuration);
        }
    }
}
