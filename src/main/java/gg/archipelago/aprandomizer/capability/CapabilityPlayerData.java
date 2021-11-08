package gg.archipelago.aprandomizer.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityPlayerData {
    public static Capability<PlayerData> CAPABILITY_PLAYER_DATA = CapabilityManager.get(new CapabilityToken<>(){});


    @SubscribeEvent
    public static void RegisterPlayerData(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
    }
}
