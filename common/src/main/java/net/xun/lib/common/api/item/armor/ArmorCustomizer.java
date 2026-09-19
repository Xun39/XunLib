package net.xun.lib.common.api.item.armor;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;

/**
 * Factory interface for creating custom armor items with specialized initialization logic.
 * <p>
 * Implementations of this interface provide fine-grained control over armor item creation,
 * allowing for custom armor classes, modified durability calculations, or additional
 * properties beyond the standard {@link ArmorItem} behavior. The {@link #DEFAULT}
 * implementation delegates to the piece's own factory via
 * {@link ArmorPieceType#createItem(ArmorContext, Item.Properties)}.
 * </p>
 *
 * @see ArmorSet.Builder#withCustomizer(ArmorCustomizer)
 * @since 1.0.0
 */
public interface ArmorCustomizer {

    /**
     * The default customizer that simply uses the piece's own factory.
     * <p>
     * This instance does not override {@link #create}, so it inherits
     * the default method defined below, which calls {@link ArmorPieceType#createItem}.
     */
    ArmorCustomizer DEFAULT = new ArmorCustomizer() {};

    /**
     * Creates a fully configured armor item instance for the given piece.
     * <p>
     * Implementations are responsible for constructing the armor item with appropriate
     * durability calculation and property configuration. The returned item should be
     * ready for registration and use in-game.
     * </p>
     * <strong>Implementation Notes:</strong>
     * <ul>
     *   <li>Durability can be obtained from {@code context.material().value().getDurability(context.durabilityFactor())}.</li>
     *   <li>Properties may be modified but should not be shared between item instances.</li>
     *   <li>The provided {@code properties} have already been processed by global and per‑piece modifiers.</li>
     * </ul>
     *
     * @param piece      the armor piece type (never {@code null})
     * @param context    the armor context providing material and modifiers (never {@code null})
     * @param properties the final item properties (already modified) (never {@code null})
     * @return a fully configured armor item instance, never {@code null}
     * @throws NullPointerException if any argument is {@code null}
     */
    default Item create(ArmorPieceType piece, ArmorContext context, Item.Properties properties) {
        return piece.createItem(context, properties);
    }
}
