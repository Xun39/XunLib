package net.xun.lib.common.api.item.armor;

import net.minecraft.world.item.ArmorItem;

import java.util.List;

/**
 * Provides predefined {@link ArmorPieceType} constants for vanilla Minecraft armor pieces.
 * <p>
 * These constants match the standard armor types (helmet, chestplate, leggings, boots, body)
 * and use vanilla {@link ArmorItem} with default durability calculations.
 * </p>
 * <p>
 * The {@link #STANDARD} list contains all five pieces (including body) in order;
 * the {@link #PLAYER} list contains the four pieces worn by players (excluding body).
 * </p>
 * <p>
 * This class is not intended to be instantiated.
 * </p>
 *
 * @since 3.0.0
 */
public final class VanillaArmorPieces {
    private VanillaArmorPieces() {
    }

    public static final ArmorPieceType HELMET = ArmorPieceType.builder("_helmet")
            .vanillaType(ArmorItem.Type.HELMET)
            .build();

    public static final ArmorPieceType CHESTPLATE = ArmorPieceType.builder("_chestplate")
            .vanillaType(ArmorItem.Type.CHESTPLATE)
            .build();

    public static final ArmorPieceType LEGGINGS = ArmorPieceType.builder("_leggings")
            .vanillaType(ArmorItem.Type.LEGGINGS)
            .build();

    public static final ArmorPieceType BOOTS = ArmorPieceType.builder("_boots")
            .vanillaType(ArmorItem.Type.BOOTS)
            .build();

    public static final ArmorPieceType BODY = ArmorPieceType.builder("_body")
            .vanillaType(ArmorItem.Type.BODY)
            .build();

    /** List containing all five pieces: HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY. */
    public static final List<ArmorPieceType> STANDARD = List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS, BODY);

    /** List containing the four player-worn pieces: HELMET, CHESTPLATE, LEGGINGS, BOOTS. */
    public static final List<ArmorPieceType> PLAYER = List.of(HELMET, CHESTPLATE, LEGGINGS, BOOTS);
}
