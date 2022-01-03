package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onPlayerClone {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            event.getPlayer().teleportTo(jail.getX(), jail.getY(), jail.getZ());
        }

        //if we are leaving because the dragon is dead check if our goals are all done!
        if(event.isEndConquered() && APRandomizer.getGoalManager().isDragonDead()) {
           APRandomizer.getGoalManager().checkGoalCompletion();
        }
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if(!event.isWasDeath())
            return;

        Player player = event.getOriginal();
        LazyOptional<PlayerData> loPlayerData = player.getCapability(APCapabilities.PLAYER_INDEX);
        if (loPlayerData.isPresent()) {
            PlayerData originalPlayerData = loPlayerData.orElseThrow(AssertionError::new);
            event.getPlayer().getCapability(APCapabilities.PLAYER_INDEX).orElseThrow(AssertionError::new).setIndex(originalPlayerData.getIndex());
        }
    }
}
