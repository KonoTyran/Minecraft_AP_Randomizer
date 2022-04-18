package gg.archipelago.aprandomizer.structures;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.PostPlacementProcessor;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;

import java.util.Optional;

public class NetherVillageStructure extends StructureFeature<JigsawConfiguration> {
    public NetherVillageStructure() {
        super(JigsawConfiguration.CODEC, NetherVillageStructure::createPiecesGenerator, PostPlacementProcessor.NONE);

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

    public static Optional<PieceGenerator<JigsawConfiguration>> createPiecesGenerator(PieceGeneratorSupplier.Context<JigsawConfiguration> context) {
        try {
            WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
            worldgenrandom.setLargeFeatureSeed(context.seed(), context.chunkPos().x, context.chunkPos().z);
            int x = context.chunkPos().getMinBlockX() + worldgenrandom.nextInt(16);
            int z = context.chunkPos().getMinBlockZ() + worldgenrandom.nextInt(16);
            int seaLevel = context.chunkGenerator().getSeaLevel();
            int y = context.chunkGenerator().getSeaLevel() + worldgenrandom.nextInt(context.chunkGenerator().getGenDepth() - 2 - context.chunkGenerator().getSeaLevel());
            NoiseColumn noisecolumn = context.chunkGenerator().getBaseColumn(x, z, context.heightAccessor());
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(x, y, z);

            while (y > seaLevel) {
                BlockState blockstate = noisecolumn.getBlock(y);
                --y;
                BlockState blockstate1 = noisecolumn.getBlock(y);
                if (blockstate.isAir() && (blockstate1.is(Blocks.SOUL_SAND) || blockstate1.isFaceSturdy(EmptyBlockGetter.INSTANCE, blockpos$mutableblockpos.setY(y), Direction.UP))) {
                    break;
                }
            }
            if (y <= seaLevel) {
                return Optional.empty();
            } else if (!context.validBiome().test(context.chunkGenerator().getNoiseBiome(QuartPos.fromBlock(x), QuartPos.fromBlock(y), QuartPos.fromBlock(z)))) {
                return Optional.empty();
            }

            /*
             * Note, you are always free to make your own JigsawPlacement class and implementation of how the structure
             * should generate. It is tricky but extremely powerful if you are doing something that vanilla's jigsaw system cannot do.
             * Such as for example, forcing 3 pieces to always spawn every time, limiting how often a piece spawns, or remove the intersection limitation of pieces.
             *
             * An example of a custom JigsawPlacement.addPieces in action can be found here (warning, it is using Mojmap mappings):
             * https://github.com/TelepathicGrunt/RepurposedStructures/blob/1.18.2/src/main/java/com/telepathicgrunt/repurposedstructures/world/structures/pieces/PieceLimitedJigsawManager.java
             */

            // Return the pieces generator that is now set up so that the game runs it when it needs to create the layout of structure pieces.
            return JigsawPlacement.addPieces(
                    context, // Used for JigsawPlacement to get all the proper behaviors done.
                    PoolElementStructurePiece::new, // Needed in order to create a list of jigsaw pieces when making the structure's layout.
                    new BlockPos(x, y, z), // Position of the structure. Y value is ignored if last parameter is set to true.
                    true,  // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                    // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                    false // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
                    // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.
            );
        }
        catch (Exception e) {
            APRandomizer.LOGGER.error("uhh ohh... bad things happened." + e.getLocalizedMessage());
            return Optional.empty();
        }
    }
}