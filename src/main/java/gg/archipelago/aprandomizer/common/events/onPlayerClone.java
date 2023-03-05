package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import net.minecraft.core.BlockPos;
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
            event.getEntity().teleportTo(jail.getX(), jail.getY(), jail.getZ());
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

        event.getOriginal().reviveCaps();

        event.getEntity().getCapability(APCapabilities.PLAYER_INDEX).ifPresent(playerData -> {
            LazyOptional<PlayerData> lazyOptional = event.getOriginal().getCapability(APCapabilities.PLAYER_INDEX);
            if (lazyOptional.isPresent()) {
                playerData.setIndex(lazyOptional.orElseThrow(AssertionError::new).getIndex());
            }
            else {
                APRandomizer.LOGGER.error("unable to copy player index for player " + event.getEntity().getDisplayName().getString());
            }
        });

        event.getOriginal().invalidateCaps();
    }
}
