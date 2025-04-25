package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;

public interface ArmorConfigurator {

    ArmorConfigurator DEFAULT = (type, material, factor, props) -> {
        int durability = type.getType().getDurability(factor);
        return new ArmorItem(material, type.getType(), props.durability(durability));
    };

    ArmorItem createArmor(ArmorType type, Holder<ArmorMaterial> material, int durabilityFactor, Item.Properties props);
}
