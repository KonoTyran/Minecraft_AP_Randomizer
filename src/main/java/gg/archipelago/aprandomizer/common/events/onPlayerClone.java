package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

@Mod.EventBusSubscriber
public class onPlayerClone {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.PlayerRespawnEvent event) {

        if(APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            //((ServerPlayer)event.getEntity()).teleportTo(Objects.requireNonNull(APRandomizer.getServer().getLevel(Level.END)),jail.getX(),jail.getY(),jail.getZ(),90f,90f);
            ((ServerPlayer)event.getEntity()).changeDimension(APRandomizer.getServer().getLevel(Level.END));
            //player.teleportTo(jail.getX(),jail.getY(),jail.getZ());
            ((ServerPlayer)event.getEntity()).setGameMode(GameType.SURVIVAL);
        }
        else {
            BlockPos pos = event.getEntity().getLevel().getSharedSpawnPos();
            event.getEntity().teleportTo(pos.getX(), pos.getY(), pos.getZ());
        }

    }
}
