package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

/**
 * Utility class providing factory methods for common tool item creation patterns.
 * <p>
 * This class contains helper methods to create {@link ToolItemFactory} instances
 * for vanilla tool constructors (e.g., {@link net.minecraft.world.item.SwordItem#SwordItem(Tier, Item.Properties)}).
 * </p>
 * <p>
 * This class is not intended to be instantiated.
 * </p>
 *
 * @since 3.0.0
 */
public final class ToolItemFactories {
    private ToolItemFactories() {
    }

    /**
     * Creates a {@link ToolItemFactory} that delegates to a vanilla-style constructor
     * accepting a {@link Tier} and {@link Item.Properties}.
     * <p>
     * The returned factory ignores the piece and context other than extracting the tier.
     * </p>
     *
     * @param vanillaFactory a functional interface matching vanilla constructors
     * @return a factory that uses the given constructor
     * @throws NullPointerException if {@code vanillaFactory} is {@code null}
     */
    public static ToolItemFactory fromConstructor(VanillaItemConstructor vanillaFactory) {
        return (piece, context, properties) -> vanillaFactory.create(context.tier(), properties);
    }

    /**
     * Functional interface matching the constructor signature of most vanilla tool classes.
     */
    @FunctionalInterface
    public interface VanillaItemConstructor {
        /**
         * Creates an item using the given tier and properties.
         *
         * @param tier the material tier (never {@code null})
         * @param properties the item properties (never {@code null})
         * @return the created item, never {@code null}
         */
        Item create(Tier tier, Item.Properties properties);
    }
}
