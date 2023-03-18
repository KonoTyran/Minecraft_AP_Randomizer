package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onBlockBreak {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onExplosionEvent(ExplosionEvent.Detonate event) {
        for (BlockPos affectedBlock : event.getAffectedBlocks()) {
            APRandomizer.getLayerManager().addLayerCheck(affectedBlock.getY());
        }
    }

    @SubscribeEvent
    static void onPlayerBlockInteract(BlockEvent.BreakEvent event) {
        if(APRandomizer.isJailPlayers())
            event.setCanceled(true);

        APRandomizer.getLayerManager().addLayerCheck(event.getPos().getY());
    }
}
