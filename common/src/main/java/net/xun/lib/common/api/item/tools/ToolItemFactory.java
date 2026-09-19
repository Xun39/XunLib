package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;

/**
 * A functional interface for creating tool items.
 * <p>
 * Implementations are responsible for instantiating the actual item class
 * (e.g., {@link net.minecraft.world.item.SwordItem}) using the provided piece, context, and final properties.
 * This factory is typically used by {@link ToolPieceType} to create the item.
 * </p>
 *
 * @see ToolItemFactories
 * @since 3.0.0
 */
@FunctionalInterface
public interface ToolItemFactory {
    /**
     * Creates a tool item instance.
     *
     * @param piece      the tool piece type (never {@code null})
     * @param context    the tool context providing tier and stats (never {@code null})
     * @param properties the final item properties (never {@code null})
     * @return the created item, never {@code null}
     */
    Item create(ToolPieceType piece, ToolContext context, Item.Properties properties);
}
