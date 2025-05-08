package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.registries.LazyItemReference;
import net.xun.lib.common.api.registries.LazyRegistryReference;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.internal.misc.ModIDManager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArmorSet {

    private final String name;
    private final Holder<ArmorMaterial> material;
    private final int durabilityFactor;
    private final Item.Properties properties;
    private final ArmorConfigurator configuration;
    private final Map<ArmorType, LazyRegistryReference<? extends Item>> armors = new EnumMap<>(ArmorType.class);
    private final Registrar<Item> registrar;

    public ArmorSet(String name, Holder<ArmorMaterial> material, int durabilityFactor, Item.Properties properties, ArmorConfigurator configuration, Registrar<Item> registrar) {
        this.name = name;
        this.material = material;
        this.durabilityFactor = durabilityFactor;
        this.properties = properties;
        this.configuration = configuration;
        this.registrar = registrar;
    }

    public ArmorSet registerAll() {
        for (ArmorType type : ArmorType.values()) {
            final String fullName = name + type.getNameSuffix();

            LazyItemReference<Item> reference = new LazyItemReference<>(fullName, () -> configuration.createArmor(type, material, durabilityFactor, properties));

            registrar.register(fullName, reference::get);
            armors.put(type, reference);
        }
        return this;
    }

    public LazyItemReference<ArmorItem> getHelmet() {
        return getArmor(ArmorType.HELMET);
    }

    public LazyItemReference<ArmorItem> getChestplate() {
        return getArmor(ArmorType.CHESTPLATE);
    }

    public LazyItemReference<ArmorItem> getLeggings() {
        return getArmor(ArmorType.LEGGINGS);
    }

    public LazyItemReference<ArmorItem> getBoots() {
        return getArmor(ArmorType.BOOTS);
    }

    @SuppressWarnings("unchecked")
    private LazyItemReference<ArmorItem> getArmor(ArmorType type) {
        return (LazyItemReference<ArmorItem>) armors.get(type);
    }

    public List<Item> getAll() {
        return armors.values().stream().map(LazyRegistryReference::get).collect(Collectors.toList());
    }

    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private int durabilityFactor;
        private Item.Properties properties;
        private ArmorConfigurator configuration = ArmorConfigurator.DEFAULT;
        private final Registrar<Item> registrar;

        public Builder(String name, Holder<ArmorMaterial> material) {
            this.name = name;
            this.material = material;
            this.registrar = Registrar.create(BuiltInRegistries.ITEM, ModIDManager.getModId());
        }

        public Builder withDurabilityFactor(int durabilityFactor) {
            this.durabilityFactor = durabilityFactor;
            return this;
        }

        public Builder withItemProperties(Item.Properties properties) {
            this.properties = properties;
            return this;
        }

        public Builder withConfiguration(ArmorConfigurator configuration) {
            this.configuration = configuration;
            return this;
        }

        public ArmorSet build() {
            return new ArmorSet(this.name, this.material, this.durabilityFactor, this.properties, this.configuration, this.registrar);
        }
    }
}
