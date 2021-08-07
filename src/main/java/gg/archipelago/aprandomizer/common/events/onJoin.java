package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod.EventBusSubscriber
public class onJoin {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(APRandomizer.isRace()) {
            event.getPlayer().setGameMode(GameType.SURVIVAL);
        }
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("no .apmc file found. please stop the server,  place .apmc file in './APData/', delete the world folder, then relaunch the server.");
        else if (data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("APMC data file wrong version, Client Version: " + APRandomizer.getClientVersion() + " APMC version:" + data.client_version);
        else if (data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Current Minecraft world has been used for a previous game. please stop server, delete the world and relaunch the server.");

        APRandomizer.getAdvancementManager().syncAllAdvancements();
        Set<IRecipe<?>> restricted = APRandomizer.getRecipeManager().getRestrictedRecipes();
        Set<IRecipe<?>> granted = APRandomizer.getRecipeManager().getGrantedRecipes();
        player.resetRecipes(restricted);
        player.awardRecipes(granted);


        APRandomizer.getGoalManager().updateInfoBar();
        APRandomizer.getItemManager().catchUpPlayer(player);

        if(APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            player.teleportTo(jail.getX(),jail.getY(),jail.getZ());
            event.getPlayer().setGameMode(GameType.SURVIVAL);
        }
    }
}
