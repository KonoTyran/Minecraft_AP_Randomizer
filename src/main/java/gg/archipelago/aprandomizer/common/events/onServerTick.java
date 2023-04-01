package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class onServerTick {

    static double count = 0;
    @SubscribeEvent
    static public void serverTickEvent(TickEvent.ServerTickEvent event) {
        if(APRandomizer.isJailPlayers())
            return;
        if(++count < 20)
            return;
        count = 0;
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            double y = Math.floor(player.getY());
            Utils.sendActionBarToPlayer(player,"Current feet Y level: " + (int)y);
        }
    }
}
