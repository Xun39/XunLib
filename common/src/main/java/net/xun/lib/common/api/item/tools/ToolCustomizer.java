package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;

/**
 * Factory interface for creating custom tool items with specialized initialization.
 * <p>
 * Implementations of this interface provide fine-grained control over tool item
 * creation, enabling custom tool classes, modified attribute application, or
 * additional properties beyond standard tool behavior. The {@link #DEFAULT}
 * implementation simply delegates to the piece's own factory via
 * {@link ToolPieceType#createItem(ToolContext, Item.Properties)}.
 * </p>
 *
 * @see ToolSet.Builder#withCustomizer(ToolCustomizer)
 * @since 1.0.0
 */
public interface ToolCustomizer {

    /**
     * The default customizer that simply uses the piece's own factory.
     * <p>
     * This instance does not override {@link #create}, so it inherits
     * the default method defined below, which calls {@link ToolPieceType#createItem}.
     */
    ToolCustomizer DEFAULT = new ToolCustomizer() {};

    /**
     * Creates a fully configured tool item instance for the given piece.
     * <p>
     * Implementations may delegate to the piece's factory or construct custom items.
     * The provided {@code properties} have already been processed by both global
     * and per‑piece property modifiers; they are ready to be passed to the item constructor.
     * </p>
     *
     * @param piece      the tool piece type (never {@code null})
     * @param context    the tool context providing tier, stats, and modifiers (never {@code null})
     * @param properties the final item properties (already modified) (never {@code null})
     * @return the created item, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    default Item create(ToolPieceType piece, ToolContext context, Item.Properties properties) {
        return piece.createItem(context, properties);
    }
}
