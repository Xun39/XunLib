package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * Immutable context object holding all configuration parameters for a tool set.
 * <p>
 * This record encapsulates the set name, tier, per‑piece stats, and property modifiers
 * used during tool creation. It provides methods to apply these configurations to
 * {@link Item.Properties} and to retrieve stats for a specific piece.
 * </p>
 *
 * @param setName              the base name of the tool set
 * @param tier                 the material tier (durability, mining level)
 * @param statsByPiece         a map from piece type to its {@link ToolStats}
 * @param propertiesModifier   a global modifier for item properties
 * @param additionalAttributes a global consumer for extra attribute modifiers
 * @since 3.0.0
 */
public record ToolContext(
        String setName,
        Tier tier,
        Map<ToolPieceType, ToolStats> statsByPiece,
        UnaryOperator<Item.Properties> propertiesModifier,
        Consumer<ItemAttributeModifiers.Builder> additionalAttributes
) {
    public ToolContext {
        Objects.requireNonNull(setName, "setName");
        Objects.requireNonNull(tier, "tier");
        Objects.requireNonNull(statsByPiece, "statsByPiece");
        Objects.requireNonNull(propertiesModifier, "propertiesModifier");
        Objects.requireNonNull(additionalAttributes, "additionalAttributes");
    }

    /**
     * Retrieves the {@link ToolStats} for the given piece.
     *
     * @param piece the tool piece type
     * @return the stats
     * @throws IllegalArgumentException if no stats are defined for the piece
     */
    public ToolStats statsFor(ToolPieceType piece) {
        ToolStats stats = statsByPiece.get(piece);
        if (stats == null) {
            throw new IllegalArgumentException("Missing ToolStats for piece: " + piece.getNameSuffix());
        }
        return stats;
    }

    /**
     * Applies all property modifiers (set-level and piece-level) and builds the
     * final attribute modifiers. Returns a new {@link Item.Properties} instance.
     */
    public Item.Properties applyProperties(ToolPieceType piece, Item.Properties base) {
        Objects.requireNonNull(piece, "piece");
        Objects.requireNonNull(base, "base");

        Item.Properties props = propertiesModifier.apply(base);
        props = piece.propertiesModifier().apply(props);

        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        statsFor(piece).addBaseAttributes(tier, builder);

        additionalAttributes.accept(builder);
        piece.additionalAttributes().accept(builder);

        return props.attributes(builder.build());
    }
}
