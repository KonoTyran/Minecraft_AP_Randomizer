package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APConfiguredStructures;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import net.minecraft.data.worldgen.StructureFeatures;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@Mod.EventBusSubscriber
public class onBiomeLoad {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void biomeLoad(BiomeLoadingEvent event) {
        APMCData data = APRandomizer.getApmcData();
        if (data.state != APMCData.State.VALID)
            return;
        List<Supplier<ConfiguredStructureFeature<?, ?>>> structs = event.getGeneration().getStructures();
        List<Supplier<ConfiguredStructureFeature<?, ?>>> toadd = new ArrayList<>();
        Iterator<Supplier<ConfiguredStructureFeature<?, ?>>> iterator = event.getGeneration().getStructures().iterator();

        while (iterator.hasNext()) {
            Supplier<ConfiguredStructureFeature<?, ?>> structure = iterator.next();
            //LOGGER.info("found {} in {}",structure.get().feature.getFeatureName(),event.getName().getPath());
            if (structure.get().feature.equals(StructureFeatures.VILLAGE_PLAINS.feature)) {
                String struct1 = data.structures.get("Overworld Structure 1");
                String struct2 = data.structures.get("Overworld Structure 2");

                if (!struct1.equals("Village") && !struct2.equals("Village")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Nether Fortress" -> toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                    case "Bastion Remnant" -> toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                    case "End City" -> toadd.add(() -> StructureFeatures.END_CITY);
                }

            } else if (structure.get().feature.equals(StructureFeatures.PILLAGER_OUTPOST.feature)) {
                String struct1 = data.structures.get("Overworld Structure 1");
                String struct2 = data.structures.get("Overworld Structure 2");

                if (!struct1.equals("Pillager Outpost") && !struct2.equals("Pillager Outpost")) {
                    iterator.remove();
                }

                switch (struct2) {
                    case "Nether Fortress" -> toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                    case "Bastion Remnant" -> toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                    case "End City" -> toadd.add(() -> StructureFeatures.END_CITY);
                }
            } else if (structure.get().feature.equals(StructureFeatures.NETHER_BRIDGE.feature)) {
                String struct1 = data.structures.get("Nether Structure 1");
                String struct2 = data.structures.get("Nether Structure 2");

                if (!struct1.equals("Nether Fortress") && !struct2.equals("Nether Fortress")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Village" -> toadd.add(() -> APConfiguredStructures.VILLAGE_NETHER);
                    case "Pillager Outpost" -> toadd.add(() -> APConfiguredStructures.PILLAGER_OUTPOST_NETHER);
                    case "End City" -> toadd.add(() -> APConfiguredStructures.END_CITY_NETHER);
                }
            } else if (structure.get().feature.equals(StructureFeatures.BASTION_REMNANT.feature)) {
                String struct1 = data.structures.get("Nether Structure 1");
                String struct2 = data.structures.get("Nether Structure 2");

                if (!struct1.equals("Bastion Remnant") && !struct2.equals("Bastion Remnant")) {
                    iterator.remove();
                }

                switch (struct2) {
                    case "Village" -> toadd.add(() -> APConfiguredStructures.VILLAGE_NETHER);
                    case "Pillager Outpost" -> toadd.add(() -> APConfiguredStructures.PILLAGER_OUTPOST_NETHER);
                    case "End City" -> toadd.add(() -> APConfiguredStructures.END_CITY_NETHER);
                }
            } else if (structure.get().feature.equals(StructureFeatures.END_CITY.feature)) {
                String struct1 = data.structures.get("The End Structure");

                if (!struct1.equals("End City")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Village" -> toadd.add(() -> APConfiguredStructures.VILLAGE_NETHER);
                    case "Pillager Outpost" -> toadd.add(() -> StructureFeatures.PILLAGER_OUTPOST);
                    case "Nether Fortress" -> toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                    case "Bastion Remnant" -> toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                }
            }
        }
        structs.addAll(toadd);
    }

}
