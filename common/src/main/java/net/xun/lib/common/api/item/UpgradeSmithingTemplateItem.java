package net.xun.lib.common.api.item;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlag;
import net.minecraft.world.item.SmithingTemplateItem;
import net.xun.lib.common.api.util.CommonUtils;

import java.util.List;

/**
 * Custom smithing template item for upgrade recipes.
 * <p>
 * This class extends Minecraft's SmithingTemplateItem to provide customizable upgrade templates.
 * The template displays information about applicable items, required ingredients, and slot descriptions
 * based on the provided name and resource locations.
 * </p>
 */
public class UpgradeSmithingTemplateItem extends SmithingTemplateItem {

    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;

    // Default empty slot icons
    private static final ResourceLocation EMPTY_SLOT_HELMET = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_helmet");
    private static final ResourceLocation EMPTY_SLOT_CHESTPLATE = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_chestplate");
    private static final ResourceLocation EMPTY_SLOT_LEGGINGS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_leggings");
    private static final ResourceLocation EMPTY_SLOT_BOOTS = ResourceLocation.withDefaultNamespace("item/empty_armor_slot_boots");
    private static final ResourceLocation EMPTY_SLOT_HOE = ResourceLocation.withDefaultNamespace("item/empty_slot_hoe");
    private static final ResourceLocation EMPTY_SLOT_AXE = ResourceLocation.withDefaultNamespace("item/empty_slot_axe");
    private static final ResourceLocation EMPTY_SLOT_SWORD = ResourceLocation.withDefaultNamespace("item/empty_slot_sword");
    private static final ResourceLocation EMPTY_SLOT_SHOVEL = ResourceLocation.withDefaultNamespace("item/empty_slot_shovel");
    private static final ResourceLocation EMPTY_SLOT_PICKAXE = ResourceLocation.withDefaultNamespace("item/empty_slot_pickaxe");
    private static final ResourceLocation EMPTY_SLOT_INGOT = ResourceLocation.withDefaultNamespace("item/empty_slot_ingot");

    private final String name;

    /**
     * Constructs a new UpgradeSmithingTemplateItem.
     *
     * @param name                     The unique name identifier for this template (used for translation keys)
     * @param baseSlotEmptyIcons       List of resource locations for base slot empty icons
     * @param additionalSlotEmptyIcons List of resource locations for additional slot empty icons
     * @param requiredFeatures         Optional feature flags required for this item
     */
    public UpgradeSmithingTemplateItem(String name, List<ResourceLocation> baseSlotEmptyIcons, List<ResourceLocation> additionalSlotEmptyIcons, FeatureFlag... requiredFeatures) {

        super(
                // Applies To description (what items this template works with)
                Component.translatable(
                        Util.makeDescriptionId("item", CommonUtils.modLoc("smithing_template." + name + ".applies_to"))
                ).withStyle(DESCRIPTION_FORMAT),

                // Ingredients description (what materials are needed)
                Component.translatable(
                        Util.makeDescriptionId("item", CommonUtils.modLoc("smithing_template." + name + ".ingredients"))
                ).withStyle(DESCRIPTION_FORMAT),

                // Upgrade title (main template name)
                Component.translatable(
                        Util.makeDescriptionId("upgrade", CommonUtils.modLoc(name))
                ).withStyle(TITLE_FORMAT),

                // Base slot description (left slot explanation)
                Component.translatable(
                        Util.makeDescriptionId("item", CommonUtils.modLoc("smithing_template." + name + ".base_slot_description"))
                ),

                // Additions slot description (right slot explanation)
                Component.translatable(
                        Util.makeDescriptionId("item", CommonUtils.modLoc("smithing_template." + name + ".additions_slot_description"))
                ),
                baseSlotEmptyIcons,
                additionalSlotEmptyIcons,
                requiredFeatures
        );

        this.name = name;
    }

    /**
     * Creates a default list of empty icons for equipment slots.
     * <p>
     * This includes icons for armor pieces and tools to show what items
     * can be upgraded using this template.
     * </p>
     *
     * @return List of resource locations for equipment slot icons
     */
    public static List<ResourceLocation> createTemplateUpgradeIconList() {
        return List.of(
                EMPTY_SLOT_HELMET,
                EMPTY_SLOT_SWORD,
                EMPTY_SLOT_CHESTPLATE,
                EMPTY_SLOT_PICKAXE,
                EMPTY_SLOT_LEGGINGS,
                EMPTY_SLOT_AXE,
                EMPTY_SLOT_BOOTS,
                EMPTY_SLOT_HOE,
                EMPTY_SLOT_SHOVEL
        );
    }

    /**
     * Creates a default list of empty icons for material slots.
     * <p>
     * Typically contains a single ingot icon, but can be customized
     * for different upgrade types.
     * </p>
     *
     * @return List of resource locations for material slot icons
     */
    public static List<ResourceLocation> createTemplateUpgradeMaterialList() {
        return List.of(EMPTY_SLOT_INGOT);
    }
}