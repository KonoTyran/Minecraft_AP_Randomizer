package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APConfiguredStructures;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class BeeGroveBiomes {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void biomeLoad(BiomeLoadingEvent event) {
        ResourceLocation biomeName = event.getName();
        List<Supplier<ConfiguredStructureFeature<?, ?>>> structures = event.getGeneration().getStructures();

        // add our structure to the following biomes
        if (biomeName.equals(Biomes.PLAINS.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.SUNFLOWER_PLAINS.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.FLOWER_FOREST.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.FOREST.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.WOODED_HILLS.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.BIRCH_FOREST.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.TALL_BIRCH_FOREST.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.BIRCH_FOREST_HILLS.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);
        if (biomeName.equals(Biomes.TALL_BIRCH_HILLS.location()))
            structures.add(() -> APConfiguredStructures.BEE_GROVE);

    }

}
