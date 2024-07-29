package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.helper.DeathLink;
import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class onDeath {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    static void onDeathEvent(LivingDeathEvent event) {
        if(!APRandomizer.isConnected())
            return;
        //only trigger on player death
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        if(!APRandomizer.getAP().getSlotData().deathlink)
            return;
        //dont send deathlink if the cause of this death was a deathlink
        if(event.getSource() instanceof DeathLinkDamage)
            return;

        DeathLink.SendDeathLink(event.getSource().getLocalizedDeathMessage(player).getString(), event.getEntity().getDisplayName().getString());
        MinecraftServer server = APRandomizer.getServer();
        GameRules.BooleanValue deathMessages = server.getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
        boolean death = deathMessages.get();
        deathMessages.set(false, server);
        for (ServerPlayer serverPlayer : APRandomizer.getServer().getPlayerList().getPlayers()) {
            if (serverPlayer != player) {
                serverPlayer.hurt(new DeathLinkDamage(),Float.MAX_VALUE);
            }
        }
        deathMessages.set(death, server);

    }
}
