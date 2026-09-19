package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.xun.lib.common.api.item.ItemSet;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * A specialized {@link ItemSet} representing a collection of tools that share
 * a common {@link Tier}.
 * <p>
 * A {@code ToolSet} associates each {@link ToolPieceType} with tool-specific
 * {@link ToolStats}, while the {@link Tier} provides the shared material
 * properties used by the tools in the set.
 * </p>
 * <p>
 * Tool creation is delegated to a {@link ToolCustomizer}, allowing individual
 * sets to create specialized tool implementations without requiring
 * subclasses of {@code ToolSet}. Item properties and additional attribute
 * modifiers can also be configured globally through the builder.
 * </p>
 * <p>
 * Tools are exposed as lazy suppliers inherited from {@link ItemSet}. This
 * allows the set to be constructed independently of the final registered item
 * instances.
 * </p>
 *
 * @see ToolPieceType
 * @see ToolStats
 * @see ToolCustomizer
 * @see ToolContext
 * @since 1.0.0
 */
public class ToolSet extends ItemSet<ToolPieceType, Item> {

    private final Tier tier;
    private final Map<ToolPieceType, ToolStats> statsByPiece;
    private final ToolContext context;

    /**
     * Constructs a tool set with the specified configuration.
     * <p>
     * The supplied tool statistics are associated with their corresponding piece
     * types and are used by the tool creation factory. The supplied properties
     * modifier and additional attribute consumer are shared by all tools created
     * by this set.
     * </p>
     *
     * @param name                 the base name used to generate tool registry names
     * @param tier                 the shared material tier for the tools
     * @param pieces               the tool pieces included in the set
     * @param statsByPiece         the tool statistics associated with each piece
     * @param propertiesModifier   modifier applied to item properties during creation
     * @param additionalAttributes consumer used to add additional attribute modifiers
     * @param customizer           strategy responsible for creating the tool instances
     * @throws NullPointerException if {@code name}, {@code tier}, {@code pieces},
     *                              {@code statsByPiece}, {@code propertiesModifier},
     *                              {@code additionalAttributes}, or {@code customizer}
     *                              is {@code null}
     */
    protected ToolSet(
            String name,
            Tier tier,
            List<ToolPieceType> pieces,
            Map<ToolPieceType, ToolStats> statsByPiece,
            UnaryOperator<Item.Properties> propertiesModifier,
            Consumer<ItemAttributeModifiers.Builder> additionalAttributes,
            ToolCustomizer customizer
    ) {
        super(name, pieces, makeFactory(name, tier, statsByPiece, propertiesModifier, additionalAttributes, customizer));

        this.tier = Objects.requireNonNull(tier, "tier");
        this.statsByPiece = Collections.unmodifiableMap(new LinkedHashMap<>(statsByPiece));
        this.context = new ToolContext(name, tier, statsByPiece, propertiesModifier, additionalAttributes);
    }

    private static BiFunction<ToolPieceType, Item.Properties, Item> makeFactory(
            String name,
            Tier tier,
            Map<ToolPieceType, ToolStats> statsByPiece,
            UnaryOperator<Item.Properties> propertiesModifier,
            Consumer<ItemAttributeModifiers.Builder> additionalAttributes,
            ToolCustomizer customizer
    ) {
        ToolContext context = new ToolContext(
                name,
                tier,
                Collections.unmodifiableMap(new LinkedHashMap<>(statsByPiece)),
                propertiesModifier,
                additionalAttributes
        );

        return (piece, properties) -> {
            Item.Properties finalProperties = context.applyProperties(piece, properties);
            Item item = customizer.create(piece, context, finalProperties);

            ToolMetaDataLookup.register(item, new ToolMetaData(piece, context, customizer));

            return item;
        };
    }

    /**
     * Returns the {@link Tier} that defines the material properties for all tools in this set.
     *
     * @return the tier, never {@code null}
     */
    public Tier getTier() {
        return tier;
    }

    /**
     * Retrieves the tool stats (attack damage and speed) for a specific piece.
     *
     * @param piece the tool piece type (must belong to this set)
     * @return the stats for that piece
     * @throws IllegalArgumentException if {@code piece} is not part of this set
     */
    public ToolStats getStats(ToolPieceType piece) {
        ToolStats stats = statsByPiece.get(piece);
        if (stats == null) {
            throw new IllegalArgumentException("Unknown piece: " + piece.getNameSuffix());
        }
        return stats;
    }

    /**
     * Returns the immutable context object that holds all configuration for this tool set.
     *
     * @return the tool context, never {@code null}
     */
    public ToolContext getContext() {
        return context;
    }

    public Supplier<Item> getSword() {
        return super.get(VanillaToolPieces.SWORD);
    }

    public Supplier<Item> getAxe() {
        return super.get(VanillaToolPieces.AXE);
    }

    public Supplier<Item> getPickaxe() {
        return super.get(VanillaToolPieces.PICKAXE);
    }

    public Supplier<Item> getShovel() {
        return super.get(VanillaToolPieces.SHOVEL);
    }

    public Supplier<Item> getHoe() {
        return super.get(VanillaToolPieces.HOE);
    }

    public static Builder builder(String name, Tier tier) {
        return new Builder(name, tier);
    }

    /**
     * @since 1.0.0
     */
    public static class Builder {
        private final String name;
        private final Tier tier;
        private final List<ToolPieceType> pieces = new ArrayList<>();
        private final Map<ToolPieceType, ToolStats> statsByPiece = new LinkedHashMap<>();
        private UnaryOperator<Item.Properties> propertiesModifier = UnaryOperator.identity();
        private Consumer<ItemAttributeModifiers.Builder> additionalAttributes = builder -> {
        };
        private ToolCustomizer customizer = ToolCustomizer.DEFAULT;

        /**
         * Constructs a new builder for a tool set with the specified base name and tier.
         *
         * @param name base name for tools (e.g., "iron"), will be appended with
         *             tool-specific suffixes
         * @param tier material tier for all tools, defines base durability,
         *             mining level, and base damage
         * @throws NullPointerException     if {@code name}, {@code tier}, or
         *                                  {@code attributeHelper} is {@code null}
         * @throws IllegalArgumentException if {@code name} is empty or contains
         *                                  invalid characters
         */
        private Builder(String name, Tier tier) {
            this.name = Objects.requireNonNull(name, "name");
            this.tier = Objects.requireNonNull(tier, "tier");
        }

        /**
         * Adds a single tool piece type to the set using its default stats.
         *
         * @param piece the piece to add (must not be {@code null})
         * @return this builder
         * @throws NullPointerException     if {@code piece} is {@code null}
         * @throws IllegalArgumentException if the piece was already added
         */
        public Builder addPiece(ToolPieceType piece) {
            Objects.requireNonNull(piece, "piece");
            if (statsByPiece.containsKey(piece)) {
                throw new IllegalArgumentException("Duplicate piece: " + piece.getNameSuffix());
            }
            pieces.add(piece);
            statsByPiece.put(piece, piece.defaultStats());
            return this;
        }

        /**
         * Adds multiple tool piece types to the set.
         *
         * @param pieces the collection of pieces to add (must not be {@code null})
         * @return this builder
         * @throws NullPointerException     if the collection is {@code null}
         * @throws IllegalArgumentException if any piece is a duplicate
         */
        public Builder addPieces(Collection<ToolPieceType> pieces) {
            Objects.requireNonNull(pieces, "pieces");
            for (ToolPieceType piece : pieces) {
                addPiece(piece);
            }
            return this;
        }

        /**
         * Overrides the default stats for a previously added piece.
         *
         * @param piece the piece whose stats to override
         * @param stats the new stats (attack damage and speed)
         * @return this builder
         * @throws IllegalArgumentException if the piece has not been added
         */
        public Builder overrideStats(ToolPieceType piece, ToolStats stats) {
            if (!statsByPiece.containsKey(piece)) {
                throw new IllegalArgumentException("Unknown piece: " + piece.getNameSuffix());
            }
            statsByPiece.put(piece, new ToolStats(stats.attackDamage(), stats.attackSpeed()));
            return this;
        }

        /**
         * Sets a modifier for the {@link Item.Properties} used when creating each tool item.
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
         * Sets a custom tool creation strategy for specialized tool implementations.
         * <p>
         * This allows overriding the default tool creation behavior to implement
         * custom tool classes, modified attribute handling, or additional properties.
         * </p>
         *
         * @param customizer tool creation strategy implementation
         * @return this builder for method chaining
         * @throws NullPointerException if {@code itemFactory} is {@code null}
         * @see ToolCustomizer
         */
        public Builder withCustomizer(ToolCustomizer customizer) {
            this.customizer = Objects.requireNonNull(customizer, "customizer");
            return this;
        }

        /**
         * Constructs an immutable {@link ToolSet} with the current builder configuration.
         * <p>
         * Validates all configuration parameters and creates a new {@code ToolSet}
         * instance. The returned set is thread-safe and lazily initializes its tools.
         * </p>
         *
         * @return a new tool set with the configured properties
         * @throws IllegalStateException if required configuration is missing or invalid
         */
        public ToolSet build() {
            if (pieces.isEmpty()) {
                throw new IllegalStateException("ToolSet '" + name + "' has no pieces");
            }
            return new ToolSet(name, tier, pieces, statsByPiece, propertiesModifier, additionalAttributes, customizer);
        }
    }
}
