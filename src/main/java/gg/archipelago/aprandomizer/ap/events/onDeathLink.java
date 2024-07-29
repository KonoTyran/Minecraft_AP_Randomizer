package gg.archipelago.aprandomizer.ap.events;

import dev.koifysh.archipelago.events.DeathLinkEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;

public class onDeathLink {

    @ArchipelagoEventListener
    public static void onBounce(DeathLinkEvent event) {
        GameRules.BooleanValue showDeathMessages = APRandomizer.getServer().getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean showDeaths = showDeathMessages.get();
        if(showDeaths) {
            String cause = event.cause;
            if(cause != null && !cause.isBlank())
                Utils.sendMessageToAll(event.cause);
            else
                Utils.sendMessageToAll("This Death brought to you by "+ event.source);
        }
        showDeathMessages.set(false,APRandomizer.getServer());
        for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            player.hurt(new DeathLinkDamage() , Float.MAX_VALUE);
        }
        showDeathMessages.set(showDeaths,APRandomizer.getServer());

    }
}
