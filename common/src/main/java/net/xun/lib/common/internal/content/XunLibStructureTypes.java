package net.xun.lib.common.internal.content;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.xun.lib.common.XunLibCommon;
import net.xun.lib.common.api.registration.Registrar;
import net.xun.lib.common.api.registration.RegistryEntry;
import net.xun.lib.common.api.world.structures.TerrainAwareJigsawStructure;

public class XunLibStructureTypes {
    public static final Registrar<StructureType<?>> STRUCTURE_TYPES = XunLibCommon.REGISTRY.registrarOf(Registries.STRUCTURE_TYPE);

    public static final RegistryEntry<StructureType<?>, StructureType<TerrainAwareJigsawStructure>> TERRAIN_AWARE_JIGSAW =
            STRUCTURE_TYPES.register("terrain_aware_jigsaw", () ->  () -> TerrainAwareJigsawStructure.CODEC);

    public static void register() {}
}
