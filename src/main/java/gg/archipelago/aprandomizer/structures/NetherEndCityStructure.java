package gg.archipelago.aprandomizer.structures;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.EndCityPieces;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

import java.util.List;

import net.minecraft.world.level.levelgen.feature.StructureFeature.StructureStartFactory;

public class NetherEndCityStructure extends StructureFeature<NoneFeatureConfiguration> {
    public NetherEndCityStructure(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /**
     * This is how the worldgen code knows what to call when it
     * is time to create the pieces of the structure for generation.
     */
    @Override
    public StructureStartFactory<NoneFeatureConfiguration> getStartFactory() {
        return NetherEndCityStructure.Start::new;
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
     * in our case here we want to add enderman to our end city
     */
    private static final List<MobSpawnSettings.SpawnerData> STRUCTURE_MOBS = ImmutableList.of(
            new MobSpawnSettings.SpawnerData(EntityType.ENDERMAN, 30, 10, 15)
    );

    @Override
    public List<MobSpawnSettings.SpawnerData> getDefaultSpawnList() {
        return STRUCTURE_MOBS;
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


            /*
             * yay for custom hard coded structure gen. lets just call this and hope it works...
             */
            BlockPos blockpos = new BlockPos(x, seaLevel, z);

            Rotation rotation = Rotation.getRandom(this.random);
            EndCityPieces.startHouseTower(templateManagerIn, blockpos, rotation, this.pieces, this.random);
            //APRandomizer.LOGGER.info("generating Nether End City at {}",blockpos.toString());
            // Sets the bounds of the structure once you are finished.
            this.createBoundingBox();

        }

    }
}