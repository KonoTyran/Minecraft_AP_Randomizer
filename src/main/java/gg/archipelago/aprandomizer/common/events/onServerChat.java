package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onServerChat {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onServerChatEvent(ServerChatEvent event) {
        if(!APRandomizer.getAP().isConnected())
            return;
        ServerPlayerEntity player = event.getPlayer();

        String message = event.getMessage();
        LOGGER.debug("{} ({}): {}", player.getScoreboardName(), player.hasPermissions(1), message);

        if (!message.startsWith("!") || player.server.getProfilePermissions(player.getGameProfile()) >= 1) {
            if (message.startsWith("!"))
                APRandomizer.getAP().sendChat(message);
            else
                APRandomizer.getAP().sendChat("(" + player.getDisplayName().getString() + ") " + message);
        }
    }
}
