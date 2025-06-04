package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.xun.lib.common.api.util.LazyReference;
import net.xun.lib.common.api.registries.Registrar;
import net.xun.lib.common.api.util.CommonUtils;
import net.xun.lib.common.internal.misc.ModIDManager;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ArmorSet {

    private final Map<ArmorType, LazyReference<ArmorItem>> armors = new EnumMap<>(ArmorType.class);

    protected ArmorSet(String name, Holder<ArmorMaterial> material, int durabilityFactor, Item.Properties properties, ArmorConfigurator configuration) {
        for (ArmorType type : ArmorType.values()) {
            String fullName = name + type.getNameSuffix();

            armors.put(type, new LazyReference<>(
                    fullName,
                    () -> configuration.createArmor(type, material, durabilityFactor, properties))
            );
        }
    }

    public Map<ResourceLocation, Supplier<ArmorItem>> getItemsForRegistration() {
        Map<ResourceLocation, Supplier<ArmorItem>> items = new LinkedHashMap<>();

        for (Map.Entry<ArmorType, LazyReference<ArmorItem>> entry : armors.entrySet()) {
            ResourceLocation id = CommonUtils.modLoc(entry.getValue().getName());
            items.put(id, entry.getValue());
        }

        return items;
    }

    public Supplier<ArmorItem> getHelmet() {
        return armors.get(ArmorType.HELMET);
    }

    public Supplier<ArmorItem> getChestplate() {
        return armors.get(ArmorType.CHESTPLATE);
    }

    public Supplier<ArmorItem> getLeggings() {
        return armors.get(ArmorType.LEGGINGS);
    }

    public Supplier<ArmorItem> getBoots() {
        return armors.get(ArmorType.BOOTS);
    }

    public List<Item> getAll() {
        return armors.values().stream()
                .map(Supplier::get)
                .collect(Collectors.toList());
    }

    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private int durabilityFactor;
        private Item.Properties properties;
        private ArmorConfigurator configuration = ArmorConfigurator.DEFAULT;

        public Builder(String name, Holder<ArmorMaterial> material) {
            this.name = name;
            this.material = material;
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
            return new ArmorSet(this.name, this.material, this.durabilityFactor, this.properties, this.configuration);
        }
    }
}
