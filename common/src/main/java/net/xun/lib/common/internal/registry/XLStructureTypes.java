package net.xun.lib.common.internal.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.xun.lib.common.XunLibConstants;
import net.xun.lib.common.api.registration.Register;
import net.xun.lib.common.api.registration.RegistryHolder;
import net.xun.lib.common.api.world.structures.TerrainAwareJigsawStructure;

public class XLStructureTypes {
    public static final Register<StructureType<?>> STRUCTURE_TYPES = Register.create(Registries.STRUCTURE_TYPE, XunLibConstants.MOD_ID);

    public static final RegistryHolder<StructureType<?>, StructureType<TerrainAwareJigsawStructure>> TERRAIN_AWARE_JIGSAW =
            STRUCTURE_TYPES.register("terrain_aware_jigsaw", () ->  () -> TerrainAwareJigsawStructure.CODEC);
}
