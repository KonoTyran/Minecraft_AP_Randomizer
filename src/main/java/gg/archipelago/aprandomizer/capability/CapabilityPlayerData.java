package gg.archipelago.aprandomizer.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityPlayerData {
    @CapabilityInject(PlayerData.class)
    public static Capability<PlayerData> CAPABILITY_PLAYER_DATA = null;

    public static void register() {
        CapabilityManager.INSTANCE.register(
                PlayerData.class,
                new PlayerData.PlayerDataStorage(),
                PlayerData::new);
    }
}
