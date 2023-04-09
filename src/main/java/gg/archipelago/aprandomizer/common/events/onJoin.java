package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.ClientStatus;
import net.minecraft.core.BlockPos;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onJoin {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {

        ServerPlayer player = (ServerPlayer) event.getEntity();
        if(APRandomizer.isRace()) {
            player.setGameMode(GameType.SURVIVAL);
            APRandomizer.getServer().getPlayerList().deop(event.getEntity().getGameProfile());
        }


        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("No APMC file found, please only start the server via the APMC file.");
        else if (data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("This Seed was generated using an incompatible randomizer version.");
        else if (data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Invalid Minecraft World please only start the Minecraft server via the correct APMC file");

        if(data.state != APMCData.State.VALID)
            return;

        if(APRandomizer.getAP().isConnected() && APRandomizer.isJailPlayers()) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_READY);
        }

        ServerScoreboard scoreboard = APRandomizer.getServer().getScoreboard();
        Objective stats = scoreboard.getObjective("deaths");
        if(stats != null) {
            scoreboard.getOrCreatePlayerScore(player.getScoreboardName(), stats);
        }


        APRandomizer.getGoalManager().updateInfoBar();

        BlockPos jail = APRandomizer.getJailPosition();
        player.teleportTo(jail.getX(),jail.getY(),jail.getZ());
        player.setGameMode(GameType.SURVIVAL);
    }
}
