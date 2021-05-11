package gg.archipelago.aprandomizer.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityWorldData {
    @CapabilityInject(WorldData.class)
    public static Capability<WorldData> CAPABILITY_WORLD_DATA = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                WorldData.class,
                new WorldData.WorldDataStorage(),
                WorldData::new);
    }
}
