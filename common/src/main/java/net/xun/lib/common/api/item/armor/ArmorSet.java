package net.xun.lib.common.api.item.armor;

import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.xun.lib.common.api.item.ItemSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A specialized {@link ItemSet} representing a collection of armor pieces
 * that share a common {@link ArmorMaterial}.
 * <p>
 * An {@code ArmorSet} can contain any combination of
 * {@link ArmorPieceType} values, allowing callers to create a complete armor
 * set or a partial collection of armor pieces.
 * </p>
 * <p>
 * Armor creation is delegated to an {@link ArmorCustomizer}, allowing
 * specialized armor implementations to be supplied without requiring a
 * subclass of {@code ArmorSet}. Shared item properties and additional
 * attribute modifiers can also be configured through the builder.
 * </p>
 * <p>
 * Armor pieces are exposed through the lazy suppliers inherited from
 * {@link ItemSet}, allowing registration and item access to remain separate
 * from the initial construction of the set.
 * </p>
 *
 * @see ArmorPieceType
 * @see ArmorMaterial
 * @see ArmorCustomizer
 * @see ArmorContext
 *
 * @since 1.0.0
 */
public class ArmorSet extends ItemSet<ArmorPieceType, Item> {

    private final Holder<ArmorMaterial> material;
    private final int durabilityFactor;
    private final ArmorContext context;

    /**
     * Constructs an armor set with the specified configuration.
     * <p>
     * The supplied material, durability factor, item-property modifier,
     * additional-attribute consumer, and customizer are shared across all armor
     * pieces in the set.
     * </p>
     *
     * @param name                 the base name used to generate armor registry names
     * @param material             holder containing the armor material used by the set
     * @param pieces               the armor pieces included in the set
     * @param durabilityFactor     multiplier applied to the material's base durability
     * @param propertiesModifier   modifier applied to item properties during creation
     * @param additionalAttributes consumer used to add additional attribute modifiers
     * @param customizer           strategy responsible for creating armor instances
     *
     * @throws NullPointerException if {@code name}, {@code material}, {@code pieces},
     *                              {@code propertiesModifier},
     *                              {@code additionalAttributes}, or {@code customizer}
     *                              is {@code null}
     */
    protected ArmorSet(
            String name,
            Holder<ArmorMaterial> material,
            List<ArmorPieceType> pieces,
            int durabilityFactor,
            UnaryOperator<Item.Properties> propertiesModifier,
            Consumer<ItemAttributeModifiers.Builder> additionalAttributes,
            ArmorCustomizer customizer
    ) {
        super(name, pieces, makeFactory(name, material, durabilityFactor, propertiesModifier, additionalAttributes, customizer));

        this.material = Objects.requireNonNull(material, "material");
        this.durabilityFactor = durabilityFactor;
        this.context = new ArmorContext(name, material, durabilityFactor, propertiesModifier, additionalAttributes);
    }

    private static BiFunction<ArmorPieceType, Item.Properties, Item> makeFactory(
            String name,
            Holder<ArmorMaterial> material,
            int durabilityFactor,
            UnaryOperator<Item.Properties> propertiesModifier,
            Consumer<ItemAttributeModifiers.Builder> additionalAttributes,
            ArmorCustomizer customizer
    ) {
        ArmorContext context = new ArmorContext(
                name,
                material,
                durabilityFactor,
                propertiesModifier,
                additionalAttributes
        );

        return (piece, properties) -> {
            Item.Properties finalProperties = context.applyProperties(piece, properties);
            return customizer.create(piece, context, finalProperties);
        };
    }

    /**
     * Returns the {@link ArmorMaterial} holder that defines protection values and toughness for this set.
     *
     * @return the armor material holder, never {@code null}
     */
    public Holder<ArmorMaterial> getMaterial() {
        return material;
    }

    /**
     * Returns the durability multiplier applied to the base durability of each armor piece.
     * <p>
     * The actual durability is calculated as: {@code material.durability() * durabilityFactor}.
     * </p>
     *
     * @return the durability factor (positive integer)
     */
    public int getDurabilityFactor() {
        return durabilityFactor;
    }

    /**
     * Returns the immutable context object that holds all configuration for this armor set.
     *
     * @return the armor context, never {@code null}
     */
    public ArmorContext getContext() {
        return context;
    }

    public Supplier<Item> getHelmet() {
        return super.get(VanillaArmorPieces.HELMET);
    }
    public Supplier<Item> getChestplate() {
        return super.get(VanillaArmorPieces.CHESTPLATE);
    }
    public Supplier<Item> getLeggings() {
        return super.get(VanillaArmorPieces.LEGGINGS);
    }
    public Supplier<Item> getBoots() {
        return super.get(VanillaArmorPieces.BOOTS);
    }

    public static Builder builder(String name, Holder<ArmorMaterial> material) {
        return new Builder(name, material);
    }

    /**
     * Builder for constructing {@link ArmorSet} instances with a fluent API.
     * <p>
     * This builder enables configuration of shared properties across all armor pieces
     * in a set, including durability multipliers, item properties, and custom creation logic.
     * </p>
     * <strong>Default Values:</strong>
     * <ul>
     *   <li>Durability factor: 0 (uses material default)</li>
     *   <li>Properties supplier: {@code Item.Properties::new}</li>
     * </ul>
     *
     * <h2>Example Usage:</h2>
     *
     * <pre>{@code
     * // Create netherite armor with fire resistance
     * ArmorSet netheriteArmor = new ArmorSet.Builder("netherite", NETHERITE_MATERIAL)
     *     .withDurabilityFactor(37)  // Standard netherite multiplier
     *     .withItemPropertiesSupplier(() -> new Item.Properties()
     *         .fireResistant()
     *         .rarity(Rarity.EPIC))
     *     .build();
     * }</pre>
     *
     * @see ArmorSet
     * @see ArmorCustomizer
     * @since 1.0.0
     */
    public static class Builder {
        private final String name;
        private final Holder<ArmorMaterial> material;
        private final List<ArmorPieceType> pieces = new ArrayList<>();
        private int durabilityFactor;
        private UnaryOperator<Item.Properties> propertiesModifier = UnaryOperator.identity();
        private Consumer<ItemAttributeModifiers.Builder> additionalAttributes = builder -> {};
        private ArmorCustomizer customizer = ArmorCustomizer.DEFAULT;

        /**
         * Constructs a new builder for an armor set with the specified base name and material.
         *
         * @param name     base name for armor pieces (e.g., "iron"), will be appended with armor-specific suffixes
         * @param material armor material holder defining protection values and toughness
         * @throws NullPointerException     if {@code name} or {@code material} is {@code null}
         * @throws IllegalArgumentException if {@code name} is empty or contains invalid characters
         */
        private Builder(String name, Holder<ArmorMaterial> material) {
            this.name = Objects.requireNonNull(name, "name");
            this.material = Objects.requireNonNull(material, "material");
        }

        /**
         * Adds a single armor piece type to the set.
         *
         * @param piece the piece to add (must not be {@code null})
         * @return this builder
         * @throws NullPointerException     if {@code piece} is {@code null}
         * @throws IllegalArgumentException if the piece was already added
         */
        public Builder addPiece(ArmorPieceType piece) {
            Objects.requireNonNull(piece, "piece");
            if (pieces.contains(piece)) {
                throw new IllegalArgumentException("Duplicate piece: " + piece.getNameSuffix());
            }
            pieces.add(piece);
            return this;
        }

        /**
         * Adds multiple armor piece types to the set.
         *
         * @param pieces the collection of pieces to add (must not be {@code null})
         * @return this builder
         * @throws NullPointerException     if the collection is {@code null}
         * @throws IllegalArgumentException if any piece is a duplicate
         */
        public Builder addPieces(Collection<ArmorPieceType> pieces) {
            Objects.requireNonNull(pieces, "pieces");
            for (ArmorPieceType piece : pieces) {
                addPiece(piece);
            }
            return this;
        }

        /**
         * Sets the durability multiplier for all armor pieces.
         * <p>
         * The actual durability is computed as {@code material.durability() * factor}.
         * </p>
         *
         * @param durabilityFactor the multiplier (must be positive)
         * @return this builder
         */
        public Builder withDurabilityFactor(int durabilityFactor) {
            this.durabilityFactor = durabilityFactor;
            return this;
        }

        /**
         * Sets a modifier for the {@link Item.Properties} used when creating each armor item.
         * <p>
         * The provided function receives the default properties (initially an empty {@code Properties}
         * instance) and can modify them as needed – for example, to set fire resistance, rarity,
         * or custom durability.
         * </p>
         *
         * @param propertiesModifier a function that transforms the base properties
         * @return this builder for method chaining
         * @throws NullPointerException if {@code propertiesModifier} is {@code null}
         */
        public Builder globalPropertiesModifier(UnaryOperator<Item.Properties> propertiesModifier) {
            this.propertiesModifier = Objects.requireNonNull(propertiesModifier, "propertiesModifier");
            return this;
        }

        /**
         * Adds a consumer that can further modify the {@link ItemAttributeModifiers.Builder}
         * after the default tool attributes have been applied.
         * <p>
         * This is useful for adding extra attribute modifiers (e.g., movement speed, knockback resistance)
         * to all tools in the set.
         * </p>
         *
         * @param additionalAttributes consumer that receives the attribute builder
         * @return this builder for method chaining
         * @throws NullPointerException if {@code additionalAttributes} is {@code null}
         */
        public Builder globalAdditionalAttributes(Consumer<ItemAttributeModifiers.Builder> additionalAttributes) {
            this.additionalAttributes = Objects.requireNonNull(additionalAttributes, "additionalAttributes");
            return this;
        }

        /**
         * Sets a custom armor creation strategy for specialized armor types.
         * <p>
         * This allows overriding the default armor creation behavior to implement
         * custom armor classes, modified durability calculations, or additional
         * properties.
         * </p>
         *
         * @param customizer armor creation strategy implementation
         * @return this builder for method chaining
         * @throws NullPointerException if {@code itemFactory} is {@code null}
         * @see ArmorCustomizer
         */
        public Builder withCustomizer(ArmorCustomizer customizer) {
            this.customizer = customizer;
            return this;
        }

        /**
         * Constructs the configured {@link ArmorSet}.
         * <p>
         * Validates all configuration and creates a new ArmorSet instance with
         * the specified properties. The returned ArmorSet is immutable.
         * </p>
         *
         * @return New armor set instance
         * @throws IllegalStateException if required configuration is invalid
         */
        public ArmorSet build() {
            if (pieces.isEmpty()) {
                throw new IllegalStateException("ArmorSet '" + name + "' has no pieces");
            }
            return new ArmorSet(name, material, pieces, durabilityFactor, propertiesModifier, additionalAttributes, customizer);
        }
    }
}