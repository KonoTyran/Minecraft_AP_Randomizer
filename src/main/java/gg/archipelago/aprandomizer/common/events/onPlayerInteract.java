package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class onPlayerInteract {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent event) {
        if(event.getSide().isClient())
            return;
        //stop all right click interactions if game has not started.
        if(APRandomizer.isJailPlayers()) {
            event.setCanceled(true);
            return;
        }

        LevelChunk chunk = event.getLevel().getChunkAt(event.getPos());
        if(chunk.getPos().x != 0 || chunk.getPos().z != 0)
            event.setCanceled(true);

    }
    @SubscribeEvent(priority = EventPriority.HIGH)
    static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        double x = Math.floor(event.getPos().getX() / 16f);
        double z = Math.floor(event.getPos().getZ() / 16f);
        if(x != 0 || z != 0)
            event.setCanceled(true);
    }

    @SubscribeEvent
    static void onBlockPlaceEvent(BlockEvent.EntityPlaceEvent event) {
        double x = Math.floor(event.getPos().getX() / 16f);
        double z = Math.floor(event.getPos().getZ() / 16f);
        if(x != 0 || z != 0)
            event.setCanceled(true);
    }

}
