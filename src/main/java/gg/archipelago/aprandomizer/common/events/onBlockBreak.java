package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.*;
import net.minecraftforge.common.extensions.IForgeBlock;
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

        if(event.getLevel().getBlockState(event.getPos()).getBlock().equals(Blocks.TNT)) {
            event.setCanceled(true);
            Block tnt = event.getLevel().getBlockState(event.getPos()).getBlock();
            tnt.onCaughtFire(event.getLevel().getBlockState(event.getPos()), event.getPlayer().getLevel(),event.getPos(),null,event.getPlayer());
            event.getLevel().setBlock(event.getPos(),Blocks.AIR.defaultBlockState(),3);
        }

        if(event.getPlayer().getMainHandItem().getOrCreateTag().getBoolean("truepick")) {
            int layer = event.getPos().getY();
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    event.getLevel().destroyBlock(new BlockPos(x, layer, z),true);
                }
            }
            APRandomizer.getLayerManager().addLayerCheck(layer);
        }
    }

    @SubscribeEvent
    static void onBlockEvent(BlockEvent event) {
        APRandomizer.getLayerManager().addLayerCheck(event.getPos().getY());
    }
}
