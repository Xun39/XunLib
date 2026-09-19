package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Immutable context object holding all configuration parameters for an armor set.
 * <p>
 * This record encapsulates the set name, armor material, durability factor, and property
 * modifiers used during armor creation. It provides a method to apply these configurations
 * to {@link Item.Properties} and build the final attribute modifiers.
 * </p>
 *
 * @param setName              the base name of the armor set (never {@code null})
 * @param material             the armor material holder (protection, toughness) (never {@code null})
 * @param durabilityFactor     the multiplier for durability (must be non-negative; default 0 uses material default)
 * @param propertiesModifier   a global modifier for item properties (never {@code null})
 * @param additionalAttributes a global consumer for extra attribute modifiers (never {@code null})
 * @since 3.0.0
 */
public record ArmorContext(
        String setName,
        Holder<ArmorMaterial> material,
        int durabilityFactor,
        UnaryOperator<Item.Properties> propertiesModifier,
        Consumer<ItemAttributeModifiers.Builder> additionalAttributes
) {
    public ArmorContext {
        Objects.requireNonNull(setName, "setName");
        Objects.requireNonNull(material, "material");
        Objects.requireNonNull(propertiesModifier, "propertiesModifier");
        Objects.requireNonNull(additionalAttributes, "additionalAttributes");
    }

    /**
     * Applies all property modifiers (set-level and piece-level) and builds the
     * final attribute modifiers. Returns a new {@link Item.Properties} instance
     * with the combined attributes.
     * <p>
     * This method does not set durability; that is handled by the customizer
     * when constructing the actual {@link net.minecraft.world.item.ArmorItem}.
     * </p>
     *
     * @param piece the armor piece type (must not be {@code null})
     * @param base  the base properties (must not be {@code null})
     * @return a new properties instance with all modifications applied
     * @throws NullPointerException if either argument is {@code null}
     */
    public Item.Properties applyProperties(ArmorPieceType piece, Item.Properties base) {
        Objects.requireNonNull(piece, "piece");
        Objects.requireNonNull(base, "base");

        Item.Properties props = propertiesModifier.apply(base);
        props = piece.propertiesModifier().apply(props);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();

        additionalAttributes.accept(builder);
        piece.additionalAttributes().accept(builder);

        return props.attributes(builder.build());
    }
}
