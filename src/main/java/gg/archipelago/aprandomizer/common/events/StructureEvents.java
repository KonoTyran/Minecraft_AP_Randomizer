package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APConfiguredStructures;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.world.World;
import net.minecraft.world.gen.FlatChunkGenerator;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;

public class StructureEvents {

    static public void addDimensionalSpacing(WorldEvent.Load event) {
        ServerWorld serverWorld = (ServerWorld)event.getWorld();

        /*
         * Prevent spawning our structure in Vanilla's superflat world as
         * people seem to want their superflat worlds free of modded structures.
         * Also that vanilla superflat is really tricky and buggy to work with in my experience.
         */
        if(serverWorld.getChunkSource().getGenerator() instanceof FlatChunkGenerator &&
                serverWorld.dimension().equals(World.OVERWORLD)){
            return;
        }

        /*
         * putIfAbsent so people can override the spacing with dimension datapacks themselves if they wish to customize spacing more precisely per dimension.
         * Requires AccessTransformer  (see resources/META-INF/accesstransformer.cfg)
         *
         * NOTE: if you add per-dimension spacing configs, you can't use putIfAbsent as WorldGenRegistries.NOISE_GENERATOR_SETTINGS in FMLCommonSetupEvent
         * already added your default structure spacing to some dimensions. You would need to override the spacing with .put(...)
         * And if you want to do dimension blacklisting, you need to remove the spacing entry entirely from the map below to prevent generation safely.
         */
        Map<Structure<?>, StructureSeparationSettings> tempMap = new HashMap<>(serverWorld.getChunkSource().generator.getSettings().structureConfig());
        tempMap.putIfAbsent(APStructures.VILLAGE_NETHER.get(), DimensionStructuresSettings.DEFAULTS.get(APStructures.VILLAGE_NETHER.get()));
        serverWorld.getChunkSource().generator.getSettings().structureConfig = tempMap;
    }

    static public void onBiomeLoad(BiomeLoadingEvent event) {
        //event.getGeneration().getStructures().add(() -> APConfiguredStructures.CONFIGURED_VILLAGE_NETHER);
    }


}
