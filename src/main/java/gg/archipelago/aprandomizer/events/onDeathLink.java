package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.DeathLinkEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public class onDeathLink {

    @ArchipelagoEventListener
    public static void onDeathLink(DeathLinkEvent event) {
        if(APRandomizer.getAP().getSlotData().deathlink) {
            if(event.time == APRandomizer.getLastDeathTimestamp())
                return;

            GameRules.BooleanValue showDeathMessages = APRandomizer.getServer().getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
            boolean showDeaths = showDeathMessages.get();
            if(!showDeaths) {
                if(event.cause != null && !event.cause.isBlank())
                    Utils.sendMessageToAll(event.cause);
                else
                    Utils.sendMessageToAll("This Death brought to you by " + event.source);
            }
            showDeathMessages.set(false, APRandomizer.getServer());
            for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                player.hurt(new DeathLinkDamage(), DeathLinkDamage.KILL_DAMAGE);
            }
            showDeathMessages.set(showDeaths, APRandomizer.getServer());
        }
    }
}
