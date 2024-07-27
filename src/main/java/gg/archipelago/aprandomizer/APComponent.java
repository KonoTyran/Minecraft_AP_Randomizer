package gg.archipelago.aprandomizer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import gg.archipelago.aprandomizer.structures.NetherVillageStructure;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;


public class APComponent {

    public static final DeferredRegister<DataComponentType<?>> DEFERRED_REGISTRY_DATA_COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, APRandomizer.MODID);

    public static final RegistryObject<DataComponentType<String>> TRACKED_STRUCTURE = DEFERRED_REGISTRY_DATA_COMPONENT.register("tracked_structure", () -> Codec.STRING);

    }

}
