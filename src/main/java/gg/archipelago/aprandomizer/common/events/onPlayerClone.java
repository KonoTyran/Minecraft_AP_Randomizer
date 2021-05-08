package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onPlayerClone
{
    @SubscribeEvent
    public static void onPlayerCloneEvent (PlayerEvent.Clone event) {
        PlayerEntity player = event.getOriginal();
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if (loPlayerData.isPresent()) {
            PlayerData originalPlayerData = loPlayerData.orElseThrow(AssertionError::new);
            event.getPlayer().getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA).orElseThrow(AssertionError::new).setIndex(originalPlayerData.getIndex());
        }
    }
}
