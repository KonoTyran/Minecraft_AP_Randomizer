package gg.archipelago.aprandomizer.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.EndCityPieces;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;

import java.util.List;

public class NetherEndCityStructure extends Structure<NoFeatureConfig> {
    public NetherEndCityStructure(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    /**
     * This is how the worldgen code knows what to call when it
     * is time to create the pieces of the structure for generation.
     */
    @Override
    public  IStartFactory<NoFeatureConfig> getStartFactory() {
        return NetherEndCityStructure.Start::new;
    }

    /**
     *        : WARNING!!! DO NOT FORGET THIS METHOD!!!! :
     * If you do not override step method, your structure WILL crash the biome as it is being parsed!
     *
     * Generation stage for when to generate the structure. there are 10 stages you can pick from!
     * This surface structure stage places the structure before plants and ores are generated.
     */
    @Override
    public GenerationStage.Decoration step() {
        return GenerationStage.Decoration.UNDERGROUND_STRUCTURES;
    }

    /**
     * This method allow us to have naturally spawning mobs inside out structure
     * in our case here we want to add enderman to our end city
     */
    private static final List<MobSpawnInfo.Spawners> STRUCTURE_MOBS = ImmutableList.of(
            new MobSpawnInfo.Spawners(EntityType.ENDERMAN, 30, 10, 15)
    );

    @Override
    public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
        return STRUCTURE_MOBS;
    }


    /**
     * Handles calling up the structure's pieces class and height that structure will spawn at.
     */
    public static class Start extends StructureStart<NoFeatureConfig> {
        public Start(Structure<NoFeatureConfig> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox, int referenceIn, long seedIn) {
            super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
        }

        @Override
        public void generatePieces(DynamicRegistries dynamicRegistryManager, ChunkGenerator chunkGenerator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {

            ChunkPos chunkpos = new ChunkPos(chunkX, chunkZ);
            // Turns the chunk coordinates into actual coordinates we can use. (random position within the chunk.)
            int x = chunkpos.getMinBlockX() + this.random.nextInt(16);
            int z = chunkpos.getMinBlockZ() + this.random.nextInt(16);

            // get sea Level, or in our case lava level.
            int seaLevel = chunkGenerator.getSeaLevel();


            /*
             * yay for custom hard coded structure gen. lets just call this and hope it works...
             */
            BlockPos blockpos = new BlockPos(x, seaLevel, z);

            Rotation rotation = Rotation.getRandom(this.random);
            EndCityPieces.startHouseTower(templateManagerIn, blockpos, rotation, this.pieces, this.random);
            //APRandomizer.LOGGER.info("generating Nether End City at {}",blockpos.toString());
            // Sets the bounds of the structure once you are finished.
            this.calculateBoundingBox();

        }

    }
}

