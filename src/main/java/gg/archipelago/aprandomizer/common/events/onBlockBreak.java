package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.client.LocationManager;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.level.BlockEvent;
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
        if(APRandomizer.isJailPlayers())
            event.setCanceled(true);

        if(!APRandomizer.getApmcData().dig_hole)
            return;
    }
}
