package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.APClient.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onPlayerClone {

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            event.getPlayer().teleportTo(jail.getX(), jail.getY(), jail.getZ());
        }
        if(event.isEndConquered() && APRandomizer.getGoalManager().goalsDone()) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        Player player = event.getOriginal();
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if (loPlayerData.isPresent()) {
            PlayerData originalPlayerData = loPlayerData.orElseThrow(AssertionError::new);
            event.getPlayer().getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA).orElseThrow(AssertionError::new).setIndex(originalPlayerData.getIndex());
        }
    }
}
