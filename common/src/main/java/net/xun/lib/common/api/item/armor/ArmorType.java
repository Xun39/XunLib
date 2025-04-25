package net.xun.lib.common.api.item.armor;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;

public enum ArmorType {

    HELMET("_helmet", ArmorItem.Type.HELMET, EquipmentSlot.HEAD),
    CHESTPLATE("_chestplate", ArmorItem.Type.CHESTPLATE, EquipmentSlot.CHEST),
    LEGGINGS("_leggings", ArmorItem.Type.LEGGINGS, EquipmentSlot.LEGS),
    BOOTS("_boots", ArmorItem.Type.BOOTS, EquipmentSlot.FEET);

    private final String suffix;
    private final ArmorItem.Type type;
    private final EquipmentSlot slot;

    ArmorType(String suffix, ArmorItem.Type type, EquipmentSlot slot) {
        this.suffix = suffix;
        this.type = type;
        this.slot = slot;
    }

    public String getSuffix() {
        return suffix;
    }

    public ArmorItem.Type getType() {
        return type;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }
}
