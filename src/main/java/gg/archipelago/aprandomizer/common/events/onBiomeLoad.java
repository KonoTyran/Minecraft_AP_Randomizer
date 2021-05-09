package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APConfiguredStructures;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.*;
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
        if(data.state != APMCData.State.VALID)
            return;
        List<Supplier<StructureFeature<?, ?>>> structs = event.getGeneration().getStructures();
        List<Supplier<StructureFeature<?, ?>>> toadd = new ArrayList<>();
        Iterator<Supplier<StructureFeature<?, ?>>> iterator = event.getGeneration().getStructures().iterator();

        while(iterator.hasNext()) {
            Supplier<StructureFeature<?, ?>> structure = iterator.next();
            //LOGGER.info("found {} in {}",structure.get().feature.getFeatureName(),event.getName().getPath());
            if(structure.get().feature.equals(StructureFeatures.VILLAGE_PLAINS.feature)) {
                String struct1 = data.structures.get("Overworld Structure 1");
                String struct2 = data.structures.get("Overworld Structure 2");

                if(!struct1.equals("Village") && !struct2.equals("Village")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Nether Fortress":
                        toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                        break;
                    case "Bastion Remnant":
                        toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                        break;
                    case "End City":
                        toadd.add(() -> StructureFeatures.END_CITY);
                        break;
                }

            }
            else if(structure.get().feature.equals(StructureFeatures.PILLAGER_OUTPOST.feature)) {
                String struct1 = data.structures.get("Overworld Structure 1");
                String struct2 = data.structures.get("Overworld Structure 2");

                if(!struct1.equals("Pillager Outpost") && !struct2.equals("Pillager Outpost")) {
                    iterator.remove();
                }

                switch (struct2) {
                    case "Nether Fortress":
                        toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                        break;
                    case "Bastion Remnant":
                        toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                        break;
                    case "End City":
                        toadd.add(() -> StructureFeatures.END_CITY);
                        break;
                }
            }
            else if(structure.get().feature.equals(StructureFeatures.NETHER_BRIDGE.feature)) {
                String struct1 = data.structures.get("Nether Structure 1");
                String struct2 = data.structures.get("Nether Structure 2");

                if(!struct1.equals("Nether Fortress") && !struct2.equals("Nether Fortress")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Village":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_VILLAGE_NETHER);
                        break;
                    case "Pillager Outpost":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_PILLAGER_OUTPOST_NETHER);
                        break;
                    case "End City":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_END_CITY_NETHER);
                        break;
                }
            }
            else if(structure.get().feature.equals(StructureFeatures.BASTION_REMNANT.feature)) {
                String struct1 = data.structures.get("Nether Structure 1");
                String struct2 = data.structures.get("Nether Structure 2");

                if(!struct1.equals("Bastion Remnant") && !struct2.equals("Bastion Remnant")) {
                    iterator.remove();
                }

                switch (struct2) {
                    case "Village":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_VILLAGE_NETHER);
                        break;
                    case "Pillager Outpost":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_PILLAGER_OUTPOST_NETHER);
                        break;
                    case "End City":
                        toadd.add(() -> APConfiguredStructures.CONFIGURED_END_CITY_NETHER);
                        break;
                }
            }
            else if(structure.get().feature.equals(StructureFeatures.END_CITY.feature)) {
                String struct1 = data.structures.get("The End Structure");

                if(!struct1.equals("End City")) {
                    iterator.remove();
                }

                switch (struct1) {
                    case "Village":
                        toadd.add(() -> StructureFeatures.VILLAGE_PLAINS);
                        break;
                    case "Pillager Outpost":
                        toadd.add(() -> StructureFeatures.PILLAGER_OUTPOST);
                        break;
                    case "Nether Fortress":
                        toadd.add(() -> StructureFeatures.NETHER_BRIDGE);
                        break;
                    case "Bastion Remnant":
                        toadd.add(() -> StructureFeatures.BASTION_REMNANT);
                        break;
                }
            }
        }
        structs.addAll(toadd);
    }

}
