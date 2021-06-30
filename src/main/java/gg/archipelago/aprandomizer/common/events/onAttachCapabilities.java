package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityProviderPlayerData;
import gg.archipelago.aprandomizer.capability.CapabilityProviderWorldData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onAttachCapabilities {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAttachCapabilitiesToEntityEvent(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof ServerPlayerEntity) {
            event.addCapability(new ResourceLocation(APRandomizer.MODID + ":capability_provider_player_data"), new CapabilityProviderPlayerData());
        }
    }

    @SubscribeEvent
    static void onAttachCapabilitiesToWorldEvent(AttachCapabilitiesEvent<World> event) {
        event.addCapability(new ResourceLocation(APRandomizer.MODID + ":capability_provider_world_data"), new CapabilityProviderWorldData());
    }
}
