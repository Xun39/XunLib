package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.xun.lib.common.impl.item.PieceType;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Represents a specific type of tool piece (e.g., sword, axe) with its own naming,
 * default stats, item factory, and property modifiers.
 * <p>
 * Instances are typically defined as constants (e.g., {@link VanillaToolPieces#SWORD})
 * and used to build a {@link ToolSet}. Each piece defines how an item is created and
 * how its properties and attributes are configured.
 * </p>
 *
 * @param nameSuffix           the suffix appended to the set name to form the registry ID
 * @param defaultStats         the default attack damage and speed for this piece
 * @param itemFactory          the factory that creates the actual item instance
 * @param propertiesModifier   a per‑piece modifier for {@link Item.Properties}
 * @param additionalAttributes a per‑piece consumer for additional attribute modifiers
 * @since 3.0.0
 */
public record ToolPieceType(
        String nameSuffix,
        ToolStats defaultStats,
        ToolItemFactory itemFactory,
        UnaryOperator<Item.Properties> propertiesModifier,
        Consumer<ItemAttributeModifiers.Builder> additionalAttributes
) implements PieceType {

    public ToolPieceType {
        nameSuffix = Objects.requireNonNull(nameSuffix, "nameSuffix");
        Objects.requireNonNull(defaultStats, "defaultStats");
        Objects.requireNonNull(itemFactory, "itemFactory");
        propertiesModifier = propertiesModifier == null ? UnaryOperator.identity() : propertiesModifier;
        additionalAttributes = additionalAttributes == null ? builder -> {} : additionalAttributes;

        if (nameSuffix.isBlank()) {
            throw new IllegalArgumentException("nameSuffix cannot be blank");
        }
        if (!nameSuffix.startsWith("_")) {
            throw new IllegalArgumentException("nameSuffix must start with '_'");
        }
    }

    @Override
    public String getNameSuffix() {
        return nameSuffix;
    }

    /**
     * Creates an item using this piece's factory and the provided context/properties.
     * This method does NOT apply any properties modifiers; it expects the properties
     * to already be final.
     */
    public Item createItem(ToolContext context, Item.Properties properties) {
        return itemFactory.create(this, context, properties);
    }

    /**
     * Fluent builder for {@link ToolPieceType}.
     */
    public static Builder builder(String suffix) {
        return new Builder(suffix);
    }

    public static final class Builder {
        private final String suffix;
        private ToolStats defaultStats = ToolStats.ZERO;
        private ToolItemFactory factory;
        private UnaryOperator<Item.Properties> propertiesModifier = UnaryOperator.identity();
        private Consumer<ItemAttributeModifiers.Builder> additionalAttributes = builder -> {};
        private Class<? extends Item> itemClass;

        private Builder(String suffix) {
            this.suffix = suffix;
        }

        /**
         * Sets the default stats for this piece.
         *
         * @param stats the stats (attack damage and speed)
         * @return this builder
         * @throws NullPointerException if {@code stats} is {@code null}
         */
        public Builder defaultStats(ToolStats stats) {
            this.defaultStats = Objects.requireNonNull(stats, "stats");
            return this;
        }

        /**
         * Sets the item factory used to create the actual item instance.
         *
         * @param factory the factory (must not be {@code null})
         * @return this builder
         * @throws NullPointerException if {@code factory} is {@code null}
         */
        public Builder factory(ToolItemFactory factory) {
            this.factory = factory;
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
         * Builds and returns the {@link ToolPieceType} instance.
         *
         * @return the configured piece type
         * @throws IllegalStateException if the factory has not been set (it is required)
         */
        public ToolPieceType build() {
            return new ToolPieceType(
                    suffix,
                    defaultStats,
                    factory,
                    propertiesModifier,
                    additionalAttributes
            );
        }
    }
}
