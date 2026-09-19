package net.xun.lib.common.api.item.tools;

import net.minecraft.world.item.*;

import java.util.List;

/**
 * Provides predefined {@link ToolPieceType} constants for vanilla Minecraft tools.
 * <p>
 * These constants match the standard tool types (sword, axe, pickaxe, shovel, hoe)
 * and use vanilla item classes ({@link SwordItem}, etc.) with default stats.
 * </p>
 * <p>
 * The {@link #STANDARD} list contains all five pieces in the order they are
 * typically used for registration.
 * </p>
 * <p>
 * This class is not intended to be instantiated.
 * </p>
 *
 * @since 3.0.0
 */
public final class VanillaToolPieces {
    private VanillaToolPieces() {
    }

    public static final ToolPieceType SWORD = ToolPieceType.builder("_sword")
            .defaultStats(ToolStats.DEFAULT_SWORD)
            .factory(ToolItemFactories.fromConstructor(SwordItem::new))
            .build();

    public static final ToolPieceType AXE = ToolPieceType.builder("_axe")
            .defaultStats(ToolStats.DEFAULT_AXE)
            .factory(ToolItemFactories.fromConstructor(AxeItem::new))
            .build();

    public static final ToolPieceType PICKAXE = ToolPieceType.builder("_pickaxe")
            .defaultStats(ToolStats.DEFAULT_PICKAXE)
            .factory(ToolItemFactories.fromConstructor(PickaxeItem::new))
            .build();

    public static final ToolPieceType SHOVEL = ToolPieceType.builder("_shovel")
            .defaultStats(ToolStats.DEFAULT_SHOVEL)
            .factory(ToolItemFactories.fromConstructor(ShovelItem::new))
            .build();

    public static final ToolPieceType HOE = ToolPieceType.builder("_hoe")
            .defaultStats(ToolStats.DEFAULT_HOE)
            .factory(ToolItemFactories.fromConstructor(HoeItem::new))
            .build();

    public static final List<ToolPieceType> STANDARD = List.of(SWORD, AXE, PICKAXE, SHOVEL, HOE);
}
