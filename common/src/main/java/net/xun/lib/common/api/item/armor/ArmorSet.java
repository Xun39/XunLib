package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.xun.lib.common.internal.item.ItemRegistrar;
import net.xun.lib.common.internal.platform.RegistrationServices;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArmorSet {

    private final String name;
    private final Holder<ArmorMaterial> material;
    private final int durabilityFactor;
    private final Item.Properties properties;
    private final ArmorConfigurator configuration;
    private final Map<ArmorType, Supplier<? extends Item>> armors = new EnumMap<>(ArmorType.class);
    private final ItemRegistrar registrar;

    public ArmorSet(String name, Holder<ArmorMaterial> material, int durabilityFactor, Item.Properties properties, ArmorConfigurator configuration, ItemRegistrar registrar) {
        this.name = name;
        this.material = material;
        this.durabilityFactor = durabilityFactor;
        this.properties = properties;
        this.configuration = configuration;
        this.registrar = registrar;
    }

    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private int durabilityFactor;
        private Item.Properties properties;
        private ArmorConfigurator configuration = ArmorConfigurator.DEFAULT;
        private final ItemRegistrar registrar;

        public Builder(String name, Holder<ArmorMaterial> material) {
            this.name = name;
            this.material = material;
            this.registrar = RegistrationServices.getItemRegistrar();
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

    public ArmorSet registerAll() {
        for (ArmorType type : ArmorType.values()) {
            armors.put(type, registrar.registerItem(
                    name + type.getSuffix(), () -> configuration.createArmor(type, material, durabilityFactor, properties)
            ));
        }
        return this;
    }

    public Supplier<ArmorItem> helmet() {
        return getPiece(ArmorType.HELMET);
    }

    public Supplier<ArmorItem> chestplate() {
        return getPiece(ArmorType.CHESTPLATE);
    }

    public Supplier<ArmorItem> leggings() {
        return getPiece(ArmorType.LEGGINGS);
    }

    public Supplier<ArmorItem> boots() {
        return getPiece(ArmorType.BOOTS);
    }

    @SuppressWarnings("unchecked")
    private Supplier<ArmorItem> getPiece(ArmorType type) {
        return (Supplier<ArmorItem>) armors.get(type);
    }

    public List<Item> getAll() {
        return armors.values().stream().map(Supplier::get).collect(Collectors.toList());
    }
}
