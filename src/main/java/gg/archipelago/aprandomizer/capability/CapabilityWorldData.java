
package gg.archipelago.aprandomizer.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CapabilityWorldData {
    public static Capability<WorldData> CAPABILITY_WORLD_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    @SubscribeEvent
    public static void RegisterWorldData(RegisterCapabilitiesEvent event) {
        event.register(WorldData.class);
    }
}