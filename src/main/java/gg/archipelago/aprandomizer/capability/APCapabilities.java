package gg.archipelago.aprandomizer.capability;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import gg.archipelago.aprandomizer.capability.data.WorldData;
import gg.archipelago.aprandomizer.capability.providers.PlayerDataProvider;
import gg.archipelago.aprandomizer.capability.providers.WorldDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class APCapabilities {
    public static Capability<PlayerData> PLAYER_INDEX = CapabilityManager.get(new CapabilityToken<>(){});
    public static Capability<WorldData> WORLD_DATA = CapabilityManager.get(new CapabilityToken<>(){});


    @SubscribeEvent
    public static void RegisterPlayerData(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
        event.register(WorldData.class);
    }

    @SubscribeEvent
    static void onAttachCapabilitiesToEntityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayer) {
            event.addCapability(new ResourceLocation(APRandomizer.MODID + ":player_data"), new PlayerDataProvider());
            event.addListener(() -> {
                
            });
        }
    }

    @SubscribeEvent
    static void onAttachCapabilitiesToWorldEvent(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(new ResourceLocation(APRandomizer.MODID + ":world_data"), new WorldDataProvider());
    }
}
