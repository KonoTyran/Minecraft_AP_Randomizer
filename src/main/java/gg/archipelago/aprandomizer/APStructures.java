package gg.archipelago.aprandomizer;

import com.mojang.serialization.Codec;
import gg.archipelago.aprandomizer.structures.BeeGroveStructure;
import gg.archipelago.aprandomizer.structures.NetherEndCityStructure;
import gg.archipelago.aprandomizer.structures.NetherVillageStructure;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class APStructures {

    public static final DeferredRegister<StructureType<?>> DEFERRED_REGISTRY_STRUCTURE = DeferredRegister.create(Registries.STRUCTURE_TYPE, APRandomizer.MODID);

    public static final RegistryObject<StructureType<?>> VILLAGE_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("village_nether", () -> typeConvert(NetherVillageStructure.CODEC));
    public static final RegistryObject<StructureType<?>> END_CITY_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("end_city_nether", () -> typeConvert(NetherEndCityStructure.CODEC));
    public static final RegistryObject<StructureType<?>> PILLAGER_OUTPOST_NETHER = DEFERRED_REGISTRY_STRUCTURE.register("pillager_outpost_nether", () -> typeConvert(NetherVillageStructure.CODEC));
    public static final RegistryObject<StructureType<?>> BEE_GROVE = DEFERRED_REGISTRY_STRUCTURE.register("bee_grove", () -> typeConvert(BeeGroveStructure.CODEC));


    // Helper method to register since compiler will complain about typing if we did () -> SkyStructures.CODEC directly.
    private static <S extends Structure> StructureType<S> typeConvert(Codec<S> codec) {
        return () -> codec;
    }

    public static final TagKey<Structure> VILLAGE_TAG = TagKey.create(Registries.STRUCTURE, new ResourceLocation("aprandomizer:village"));
    public static final TagKey<Structure> OUTPOST_TAG = TagKey.create(Registries.STRUCTURE, new ResourceLocation("aprandomizer:pillager_outpost"));
    public static final TagKey<Structure> END_CITY_TAG = TagKey.create(Registries.STRUCTURE, new ResourceLocation("aprandomizer:end_city"));
    public static final TagKey<Structure> BASTION_REMNANT_TAG = TagKey.create(Registries.STRUCTURE, new ResourceLocation("aprandomizer:bastion_remnant"));
    public static final TagKey<Structure> FORTRESS_TAG = TagKey.create(Registries.STRUCTURE, new ResourceLocation("aprandomizer:fortress"));


}
