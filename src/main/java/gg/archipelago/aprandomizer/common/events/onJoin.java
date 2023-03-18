package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameType;
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
        if(APRandomizer.isRace())
                player.setGameMode(GameType.SURVIVAL);

        MobEffectInstance saturation = new MobEffectInstance(MobEffects.SATURATION,MobEffectInstance.INFINITE_DURATION,1,false,false,false);
        player.addEffect(saturation);

        MobEffectInstance nightVision = new MobEffectInstance(MobEffects.NIGHT_VISION,MobEffectInstance.INFINITE_DURATION,1,false,false,false);
        player.addEffect(nightVision);

        APMCData data = APRandomizer.getApmcData();
        if (data.state == APMCData.State.MISSING)
            Utils.sendMessageToAll("No APMC file found, please only start the server via the APMC file.");
        else if (data.state == APMCData.State.INVALID_VERSION)
            Utils.sendMessageToAll("This Seed was generated using an incompatible randomizer version.");
        else if (data.state == APMCData.State.INVALID_SEED)
            Utils.sendMessageToAll("Invalid Minecraft World please only start the Minecraft server via the correct APMC file");


        APRandomizer.getGoalManager().updateInfoBar();
        APRandomizer.getItemManager().catchUpPlayer(player);

        if(APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            player.teleportTo(jail.getX(),jail.getY(),jail.getZ());
            player.setGameMode(GameType.SURVIVAL);
        }
    }
}
