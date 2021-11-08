package gg.archipelago.aprandomizer.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Registry;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.structures.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.List;

import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

public class NetherVillageStructure extends StructureFeature<NoneFeatureConfiguration> {
    public NetherVillageStructure(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /**
     * This is how the worldgen code knows what to call when it
     * is time to create the pieces of the structure for generation.
     */
    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return NetherVillageStructure.Start::new;
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

    /**
     * This method allow us to have naturally spawning mobs inside out structure
     * in our case here we want to add cats to our village
     */
    private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_CREATURES = ImmutableList.of(
            new MobSpawnSettings.SpawnerData(EntityType.CAT, 30, 10, 15)
    );

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultCreatureSpawnList() {
        return STRUCTURE_CREATURES;
    }


    /**
     * Handles calling up the structure's pieces class and height that structure will spawn at.
     */
    public static class Start extends StructureStart<NoneFeatureConfiguration> {
        public Start(StructureFeature<NoneFeatureConfiguration> structureIn, ChunkPos chunkPos, int referenceIn, long seedIn) {
            super(structureIn, chunkPos, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(RegistryAccess dynamicRegistryManager, ChunkGenerator chunkGenerator, StructureManager templateManagerIn, ChunkPos chunkPos, Biome biomeIn, NoneFeatureConfiguration config, LevelHeightAccessor levelHeightAccessor) {
            // Turns the chunk coordinates into actual coordinates we can use. (random position within the chunk.)
            int x = chunkPos.getMinBlockX() + this.random.nextInt(16);
            int z = chunkPos.getMinBlockZ() + this.random.nextInt(16);

            // get sea Level, or in our case lava level.
            int seaLevel = chunkGenerator.getSeaLevel();

            int y = seaLevel + this.random.nextInt(chunkGenerator.getGenDepth() - 2 - seaLevel);


            /*
             * If you are doing Nether structures, you'll probably want to spawn your structure on top of ledges.
             * Best way to do that is to use getBaseColumn to grab a column of blocks at the structure's x/z position.
             * Then loop through it and look for land with air above it and set blockpos's Y value to it.
             * Make sure to set the final boolean in JigsawManager.addPieces to false so
             * that the structure spawns at blockpos's y value instead of placing the structure on the Bedrock roof!
             */
            NoiseColumn iblockreader = chunkGenerator.getBaseColumn(chunkPos.x,chunkPos.z,levelHeightAccessor);
            for (BlockPos.MutableBlockPos blockpos$mutable = new BlockPos.MutableBlockPos(x, y, z); y > seaLevel; --y) {
                BlockState blockstate = iblockreader.getBlockState(blockpos$mutable);
                blockpos$mutable.move(Direction.DOWN);
                BlockState blockstate1 = iblockreader.getBlockState(blockpos$mutable);
                if (blockstate.isAir() && (!blockstate1.isAir())) {
                    break;
                }
            }

            /*
             * We pass this into addPieces to tell it where to generate the structure.
             * If addPieces's last parameter is true, blockpos's Y value is ignored and the
             * structure will spawn at terrain height instead. Set that parameter to false to
             * force the structure to spawn at blockpos's Y value instead. You got options here!
             */

            //force end citites to spawn at LEAST y=64 if they are in the end.
            if( (biomeIn.getRegistryName().toString().equals(Biomes.END_HIGHLANDS.location().toString())
                    || biomeIn.getRegistryName().toString().equals(Biomes.END_MIDLANDS.location().toString()))
                    && y <= 50 ) {
                y = 50;
            }
            BlockPos blockpos = new BlockPos(x, y, z);

            // All a structure has to do is call this method to turn it into a jigsaw based structure!
            JigsawPlacement.addPieces(
                    dynamicRegistryManager,
                    new JigsawConfiguration(() -> dynamicRegistryManager.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY)
                            // The path to the starting Template Pool JSON file to read.
                            //
                            // Note, this is "structure_tutorial:run_down_house/start_pool" which means
                            // the game will automatically look into the following path for the template pool:
                            // "resources/data/structure_tutorial/worldgen/template_pool/run_down_house/start_pool.json"
                            // This is why your pool files must be in "data/<modid>/worldgen/template_pool/<the path to the pool here>"
                            // because the game automatically will check in worldgen/template_pool for the pools.
                            .get(new ResourceLocation(APRandomizer.MODID, "village/nether/town_centers")),

                            // How many pieces outward from center can a recursive jigsaw structure spawn.
                            // Our structure is only 1 piece outward and isn't recursive so any value of 1 or more doesn't change anything.
                            // However, I recommend you keep this a decent value like 10 so people can use datapacks to add additional pieces to your structure easily.
                            // But don't make it too large for recursive structures like villages or you'll crash server due to hundreds of pieces attempting to generate!
                            6),
                    PoolElementStructurePiece::new,
                    chunkGenerator,
                    templateManagerIn,
                    blockpos, // Position of the structure. Y value is ignored if last parameter is set to true.
                    this, // The list that will be populated with the jigsaw pieces after this method.
                    this.random,
                    true, // Special boundary adjustments for villages. It's... hard to explain. Keep this false and make your pieces not be partially intersecting.
                    // Either not intersecting or fully contained will make children pieces spawn just fine. It's easier that way.
                    false,
                    levelHeightAccessor);  // Place at heightmap (top land). Set this to false for structure to be place at the passed in blockpos's Y value instead.
            // Definitely keep this false when placing structures in the nether as otherwise, heightmap placing will put the structure on the Bedrock roof.


            // **THE FOLLOWING TWO LINES ARE OPTIONAL**
            //
            // Right here, you can do interesting stuff with the pieces in this.pieces such as offset the
            // center piece by 50 blocks up for no reason, remove repeats of a piece or add a new piece so
            // only 1 of that piece exists, etc. But you do not have access to the piece's blocks as this list
            // holds just the piece's size and positions. Blocks will be placed later in JigsawManager.
            //
            // In this case, we do `piece.offset` to raise pieces up by 1 block so that the house is not right on
            // the surface of water or sunken into land a bit.
            //
            // Then we extend the bounding box down by 1 by doing `piece.getBoundingBox().minY` which will cause the
            // land formed around the structure to be lowered and not cover the doorstep. You can raise the bounding
            // box to force the structure to be buried as well. This bounding box stuff with land is only for structures
            // that you added to Structure.NOISE_AFFECTING_FEATURES field handles adding land around the base of structures.
            //
            // By lifting the house up by 1 and lowering the bounding box, the land at bottom of house will now be
            // flush with the surrounding terrain without blocking off the doorstep.
            //this.pieces.forEach(piece -> piece.move(0, 0, 0));


            // Sets the bounds of the structure once you are finished.
            this.createBoundingBox();

        }
    }
}