package gg.archipelago.aprandomizer;

import gg.archipelago.aprandomizer.structures.BeeGroveStructure;
import gg.archipelago.aprandomizer.structures.NetherEndCityStructure;
import gg.archipelago.aprandomizer.structures.NetherPillagerOutpostStructure;
import gg.archipelago.aprandomizer.structures.NetherVillageStructure;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;


public class APStructures {

    /**
     * We are using the Deferred Registry system to register our structure as this is the preferred way on Forge.
     * This will handle registering the base structure for us at the correct time, so we don't have to handle it ourselves.
     *
     * HOWEVER, do note that Deferred Registries only work for anything that is a Forge Registry. This means that
     * configured structures and configured features need to be registered directly to BuiltinRegistries as there
     * is no Deferred Registry system for them.
     */
    public static final DeferredRegister<StructureFeature<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(ForgeRegistries.STRUCTURE_FEATURES, APRandomizer.MODID);


    /**
     * Registers the base structure itself and sets what its path is.
     */
    public static final RegistryObject<StructureFeature<?>> VILLAGE_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("village_nether", NetherVillageStructure::new);
    public static final RegistryObject<StructureFeature<?>> END_CITY_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("end_city_nether", NetherEndCityStructure::new);
    public static final RegistryObject<StructureFeature<?>> PILLAGER_OUTPOST_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("pillager_outpost_nether", NetherPillagerOutpostStructure::new);
    public static final RegistryObject<StructureFeature<?>> BEE_GROVE = DEFERRED_REGISTRY_STRUCTURE.register("bee_grove", BeeGroveStructure::new);


    public static final TagKey<ConfiguredStructureFeature<?,?>> VILLAGE_TAG = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("aprandomizer:village"));
    public static final TagKey<ConfiguredStructureFeature<?,?>> OUTPOST_TAG = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("aprandomizer:pillager_outpost"));
    public static final TagKey<ConfiguredStructureFeature<?,?>> END_CITY_TAG = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("aprandomizer:end_city"));
    public static final TagKey<ConfiguredStructureFeature<?,?>> BASTION_REMNANT_TAG = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("aprandomizer:bastion_remnant"));
    public static final TagKey<ConfiguredStructureFeature<?,?>> FORTRESS_TAG = TagKey.create(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY, new ResourceLocation("aprandomizer:fortress"));


}
