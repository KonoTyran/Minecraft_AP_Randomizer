package gg.archipelago.aprandomizer.structures;

import com.google.common.collect.Lists;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

import java.util.List;
import java.util.Optional;

public class NetherEndCityStructure extends StructureFeature<NoneFeatureConfiguration> {
    public NetherEndCityStructure() {
        super(NoneFeatureConfiguration.CODEC, NetherEndCityStructure::pieceGeneratorSupplier, PostPlacementProcessor.NONE);
    }

    private static boolean isFeatureChunk(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
        return context.validBiomeOnTop(Heightmap.Types.WORLD_SURFACE_WG);
    }


    /**
     * : WARNING!!! DO NOT FORGET THIS METHOD!!!! :
     * If you do not override step method, your structure WILL crash the biome as it is being parsed!
     * <p>
     * Generation stage for when to generate the structure. there are 10 stages you can pick from!
     * This surface structure stage places the structure before plants and ores are generated.
     */
    @Override
    public GenerationStep.Decoration step() {
        return GenerationStep.Decoration.UNDERGROUND_STRUCTURES;
    }

    private static Optional<PieceGenerator<NoneFeatureConfiguration>> pieceGeneratorSupplier(PieceGeneratorSupplier.Context<NoneFeatureConfiguration> context) {
        // get sea Level, or in our case lava level.
        int seaLevel = context.chunkGenerator().getSeaLevel();

        /*
         * yay for custom hard coded structure gen. let's just call this and hope it works...
         */
        BlockPos blockpos = context.chunkPos().getMiddleBlockPosition(seaLevel);

        // Check if the spot is valid for our structure. This is just as another method for cleanness.
        // Returning an empty optional tells the game to skip this spot as it will not generate the structure.
        if (!NetherEndCityStructure.isFeatureChunk(context)) {
            return Optional.empty();
        }

        return Optional.of((builder, context1) -> {
            Rotation rotation = Rotation.getRandom(context1.random());
            List<StructurePiece> list = Lists.newArrayList();
            EndCityPieces.startHouseTower(context1.structureManager(), blockpos, rotation, list, context1.random());
            list.forEach(builder::addPiece);
        });

    }

}