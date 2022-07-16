package gg.archipelago.aprandomizer.common.events;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.*;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.data.JsonCodecProvider;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;

@Mod.EventBusSubscriber
public class onDataGather {

    private static final Codec<HolderSet<Structure>> STRUCTURE_LIST_CODEC = RegistryCodecs.homogeneousList(Registry.STRUCTURE_REGISTRY, Structure.DIRECT_CODEC);
    public static final String MODIFY_STRONGHOLD = "modify_stronghold";
    public static final String TEST = "test";
    public static final ResourceLocation ADD_SPAWNS_TO_STRUCTURE_RL = new ResourceLocation(APRandomizer.MODID, TEST);
    public static final ResourceLocation MODIFY_STRONGHOLD_RL = new ResourceLocation(APRandomizer.MODID, MODIFY_STRONGHOLD);

    @SubscribeEvent
    public void onGatherData(GatherDataEvent event) {
        // Example of how to datagen datapack registry objects.
        DataGenerator generator = event.getGenerator();
        final RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, RegistryAccess.BUILTIN.get());

        // prepare to datagenerate our structure modifier
        final StructureModifier structureModifier = new TestModifier(
                HolderSet.direct(ops.registry(Registry.STRUCTURE_REGISTRY).get().getHolder(BuiltinStructures.STRONGHOLD).orElseThrow()),
                MobCategory.MONSTER,
                new MobSpawnSettings.SpawnerData(EntityType.WITHER_SKELETON, 100, 5, 15)
        );

        DataProvider structureModifierProvider =
                JsonCodecProvider.forDatapackRegistry(generator, event.getExistingFileHelper(), APRandomizer.MODID, ops, ForgeRegistries.Keys.STRUCTURE_MODIFIERS,
                        Map.of(MODIFY_STRONGHOLD_RL, structureModifier));
        generator.addProvider(event.includeServer(), structureModifierProvider);
    }


    public record TestModifier(HolderSet<Structure> structures, MobCategory category, MobSpawnSettings.SpawnerData spawn)
            implements StructureModifier
    {
        private static final RegistryObject<Codec<? extends StructureModifier>> SERIALIZER = RegistryObject.create(ADD_SPAWNS_TO_STRUCTURE_RL, ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, APRandomizer.MODID);

        @Override
        public void modify(Holder<Structure> structure, Phase phase, ModifiableStructureInfo.StructureInfo.Builder builder)
        {
            if (phase == Phase.ADD && this.structures.contains(structure))
            {
                builder.getStructureSettings()
                        .getOrAddSpawnOverrides(category)
                        .addSpawn(spawn);
            }
        }

        @Override
        public Codec<? extends StructureModifier> codec()
        {
            return SERIALIZER.get();
        }

        public static Codec<TestModifier> makeCodec()
        {
            return RecordCodecBuilder.create(builder -> builder.group(
                    STRUCTURE_LIST_CODEC.fieldOf("structures").forGetter(TestModifier::structures),
                    MobCategory.CODEC.fieldOf("category").forGetter(TestModifier::category),
                    MobSpawnSettings.SpawnerData.CODEC.fieldOf("spawn").forGetter(TestModifier::spawn)
            ).apply(builder, TestModifier::new));
        }
    }
}
