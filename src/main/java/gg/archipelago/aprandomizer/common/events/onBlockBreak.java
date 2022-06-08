package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onBlockBreak {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerBlockInteract(BlockEvent.BreakEvent event) {
        if(!APRandomizer.isJailPlayers())
            return;
        event.setCanceled(true);
        event.getPlayer().sendSystemMessage(Component.literal("No!"));
    }
}
