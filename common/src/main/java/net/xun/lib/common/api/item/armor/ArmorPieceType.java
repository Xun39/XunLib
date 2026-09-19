package net.xun.lib.common.api.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.xun.lib.common.impl.item.PieceType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Represents a specific type of armor piece (e.g., helmet, chestplate) with its own naming,
 * vanilla type, customizer, and property modifiers.
 * <p>
 * Instances are typically defined as constants (e.g., {@link VanillaArmorPieces#HELMET})
 * and used to build an {@link ArmorSet}. Each piece defines how an item is created and
 * how its properties and attributes are configured.
 * </p>
 *
 * @param nameSuffix           the suffix appended to the set name to form the registry ID
 *                             (must start with '_' and not be blank)
 * @param vanillaType          the corresponding {@link ArmorItem.Type} (never {@code null})
 * @param customizer           optional customizer override (may be {@code null}, then uses set-level)
 * @param propertiesModifier   a per‑piece modifier for {@link Item.Properties} (never {@code null})
 * @param additionalAttributes a per‑piece consumer for additional attribute modifiers (never {@code null})
 * @since 3.0.0
 */
public record ArmorPieceType(
        String nameSuffix,
        ArmorItem.Type vanillaType,
        ArmorCustomizer customizer,
        UnaryOperator<Item.Properties> propertiesModifier,
        Consumer<ItemAttributeModifiers.Builder> additionalAttributes
) implements PieceType {

    public ArmorPieceType {
        nameSuffix = Objects.requireNonNull(nameSuffix, "nameSuffix");
        Objects.requireNonNull(vanillaType, "vanillaType");
        propertiesModifier = propertiesModifier == null ? UnaryOperator.identity() : propertiesModifier;
        additionalAttributes = additionalAttributes == null ? builder -> {} : additionalAttributes;

        if (nameSuffix.isBlank()) {
            throw new IllegalArgumentException("nameSuffix cannot be blank");
        }
        if (!nameSuffix.startsWith("_")) {
            throw new IllegalArgumentException("nameSuffix must start with '_', got: " + nameSuffix);
        }
    }

    @Override
    public String getNameSuffix() {
        return nameSuffix;
    }

    /**
     * Creates a standard {@link ArmorItem} using the context's material and the vanilla type.
     * <p>
     * This method does not apply durability; it is expected that the caller (usually the
     * {@link ArmorCustomizer}) will set the durability on the properties.
     * </p>
     *
     * @param context    the armor context (never {@code null})
     * @param properties the final item properties (never {@code null})
     * @return a new {@link ArmorItem} instance
     */
    public Item createItem(ArmorContext context, Item.Properties properties) {
        return new ArmorItem(context.material(), vanillaType, properties);
    }

    /**
     * Fluent builder for {@link ArmorPieceType}.
     */
    public static Builder builder(String suffix) {
        return new Builder(suffix);
    }

    public static final class Builder {
        private final String suffix;
        private ArmorItem.Type vanillaType;
        private ArmorCustomizer customizer = null;
        private UnaryOperator<Item.Properties> propertiesModifier = UnaryOperator.identity();
        private Consumer<ItemAttributeModifiers.Builder> additionalAttributes = builder -> {};

        private Builder(String suffix) {
            this.suffix = suffix;
        }

        /**
         * Sets the vanilla armor type for this piece.
         *
         * @param type the {@link ArmorItem.Type} (must not be {@code null})
         * @return this builder
         * @throws NullPointerException if {@code type} is {@code null}
         */
        public Builder vanillaType(ArmorItem.Type type) {
            this.vanillaType = Objects.requireNonNull(type, "type");
            return this;
        }

        /**
         * Sets a customizer override for this specific piece.
         * <p>
         * If set, this customizer will be used instead of the set-level customizer
         * when creating this piece.
         * </p>
         *
         * @param customizer the customizer (may be {@code null} to use the set-level one)
         * @return this builder
         */
        public Builder customizer(ArmorCustomizer customizer) {
            this.customizer = customizer;
            return this;
        }

        /**
         * Sets a per‑piece modifier for item properties.
         *
         * @param propertiesModifier the modifier (must not be {@code null})
         * @return this builder
         * @throws NullPointerException if {@code propertiesModifier} is {@code null}
         */
        public Builder propertiesModifier(UnaryOperator<Item.Properties> propertiesModifier) {
            this.propertiesModifier = Objects.requireNonNull(propertiesModifier, "propertiesModifier");
            return this;
        }

        /**
         * Sets a per‑piece consumer for additional attribute modifiers.
         *
         * @param additionalAttributes the consumer (must not be {@code null})
         * @return this builder
         * @throws NullPointerException if {@code additionalAttributes} is {@code null}
         */
        public Builder additionalAttributes(Consumer<ItemAttributeModifiers.Builder> additionalAttributes) {
            this.additionalAttributes = Objects.requireNonNull(additionalAttributes, "additionalAttributes");
            return this;
        }

        /**
         * Builds and returns the {@link ArmorPieceType} instance.
         *
         * @return the configured piece type
         * @throws IllegalStateException if the vanilla type has not been set (it is required)
         */
        public ArmorPieceType build() {
            return new ArmorPieceType(
                    suffix,
                    vanillaType,
                    customizer,
                    propertiesModifier,
                    additionalAttributes
            );
        }
    }
}
