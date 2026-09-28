package net.xun.lib.common.api.world.structures;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.xun.lib.common.registry.XLStructureTypes;

import java.util.Optional;

public class TerrainAwareJigsawStructure extends Structure {
    public static final MapCodec<TerrainAwareJigsawStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                TerrainAwareJigsawStructure.settingsCodec(instance),
                StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(s -> s.startPool),
                ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(s -> s.startJigsawName),
                Codec.intRange(0, 20).fieldOf("size").forGetter(s -> s.size),
                HeightProvider.CODEC.fieldOf("start_height").forGetter(s -> s.startHeight),
                Codec.BOOL.fieldOf("use_expansion_hack").forGetter(s -> s.useExpansionHack),
                Codec.INT.fieldOf("max_distance_from_center").forGetter(s -> s.maxDistanceFromCenter),
                DimensionPadding.CODEC.fieldOf("dimension_padding").forGetter(s -> s.dimensionPadding),
                LiquidSettings.CODEC.fieldOf("liquid_settings").forGetter(s -> s.liquidSettings),
                TerrainPlacement.CODEC.fieldOf("terrain_placement").forGetter(s -> s.terrainPlacement)
            ).apply(instance, TerrainAwareJigsawStructure::new)
    );

    protected final Holder<StructureTemplatePool> startPool;
    protected final Optional<ResourceLocation> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final boolean useExpansionHack;
    protected final int maxDistanceFromCenter;
    protected final DimensionPadding dimensionPadding;
    protected final LiquidSettings liquidSettings;
    protected final TerrainPlacement terrainPlacement;

    public TerrainAwareJigsawStructure(
            StructureSettings settings,
            Holder<StructureTemplatePool> startPool,
            Optional<ResourceLocation> startJigsawName,
            int size,
            HeightProvider startHeight,
            boolean useExpansionHack,
            int maxDistanceFromCenter,
            DimensionPadding dimensionPadding,
            LiquidSettings liquidSettings,
            TerrainPlacement terrainPlacement
    ) {
        super(settings);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.useExpansionHack = useExpansionHack;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
        this.dimensionPadding = dimensionPadding;
        this.liquidSettings = liquidSettings;
        this.terrainPlacement = terrainPlacement;
    }

    @Override
    protected Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        ChunkPos chunkPos = context.chunkPos();

        int originX = chunkPos.getMinBlockX();
        int originZ = chunkPos.getMinBlockZ();

        TerrainInfo terrain = findTerrain(context, originX, originZ, terrainPlacement);
        if (!shouldGenerate(context, originX, originZ, terrain)) {
            return Optional.empty();
        }

        int generatedY = calculateStartHeight(context, terrain);
        BlockPos startPos = createStartPos(context, originX, generatedY, originZ, terrain);

        return JigsawPlacement.addPieces(
                context,
                startPool,
                startJigsawName,
                size,
                startPos,
                useExpansionHack,
                Optional.empty(),
                maxDistanceFromCenter,
                PoolAliasLookup.EMPTY,
                dimensionPadding,
                liquidSettings
        );
    }

    /**
     * Finds the terrain information used by this structure.
     * Override this when a subclass needs a completely different terrain
     * matching algorithm.
     */
    protected static TerrainInfo findTerrain(GenerationContext context, int originX, int originZ, TerrainPlacement placement) {
        int minHeight = Integer.MAX_VALUE;
        int maxHeight = Integer.MIN_VALUE;

        for (int x = placement.minX(); x <= placement.maxX(); x += placement.sampleStep()) {
            for (int z = placement.minZ(); z <= placement.maxZ(); z += placement.sampleStep()) {
                int worldX = originX + x;
                int worldZ = originZ + z;

                int height = context.chunkGenerator().getFirstOccupiedHeight(
                        worldX,
                        worldZ,
                        placement.heightmap(),
                        context.heightAccessor(),
                        context.randomState()
                );

                minHeight = Math.min(minHeight, height);
                maxHeight = Math.max(maxHeight, height);
            }
        }

        return new TerrainInfo(minHeight, maxHeight, maxHeight - minHeight);
    }

    /**
     * Validates whether the sampled terrain variance is within acceptable bounds.
     */
    protected boolean shouldGenerate(GenerationContext context, int originX, int originZ, TerrainInfo terrain) {
        return terrain.variance() <= terrainPlacement.maxVariance();
    }

    /**
     * Calculates the Y coordinate of the structure's origin.
     * Override this when the subclass needs different vertical placement.
     */
    protected int calculateStartHeight(GenerationContext context, TerrainInfo terrain) {
        return terrain.minimumHeight()
                + startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()))
                + terrainPlacement.verticalOffset();
    }

    /**
     * Creates the position passed to JigsawPlacement.
     * Override this when the structure needs to shift or otherwise modify
     * the final origin position.
     */
    protected BlockPos createStartPos(GenerationContext context, int originX, int generatedY, int originZ, TerrainInfo terrain) {
        return new BlockPos(originX, generatedY, originZ);
    }

    @Override
    public StructureType<?> type() {
        return XLStructureTypes.TERRAIN_AWARE_JIGSAW.get();
    }

    public record TerrainPlacement(int minX, int maxX, int minZ, int maxZ, int maxVariance, int sampleStep,
                                   int verticalOffset, Heightmap.Types heightmap) {
        public static final MapCodec<TerrainPlacement> CODEC =
                RecordCodecBuilder.mapCodec(instance -> instance.group(
                        Codec.INT.fieldOf("min_x").forGetter(TerrainPlacement::minX),
                        Codec.INT.fieldOf("max_x").forGetter(TerrainPlacement::maxX),
                        Codec.INT.fieldOf("min_z").forGetter(TerrainPlacement::minZ),
                        Codec.INT.fieldOf("max_z").forGetter(TerrainPlacement::maxZ),
                        Codec.INT.fieldOf("max_variance").forGetter(TerrainPlacement::maxVariance),
                        Codec.intRange(1, 16).fieldOf("sample_step").forGetter(TerrainPlacement::sampleStep),
                        Codec.INT.fieldOf("vertical_offset").forGetter(TerrainPlacement::verticalOffset),
                        Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(TerrainPlacement::heightmap)
                ).apply(instance, TerrainPlacement::new));
    }

    public record TerrainInfo(int minimumHeight, int maximumHeight, int variance) {
    }
}
