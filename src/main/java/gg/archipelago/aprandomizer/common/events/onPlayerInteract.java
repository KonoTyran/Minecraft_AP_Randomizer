package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class onPlayerInteract {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent event) {
        //stop all right click interactions if game has not started.
        if(APRandomizer.isJailPlayers())
            event.setCanceled(true);
    }

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().hasTag()) {
            return;
        }

        BlockState block = event.getWorld().getBlockState(event.getHitVec().getBlockPos());
        if(event.getItemStack().getTag().get("structure") != null && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getPlayer().getServer().execute(() -> {
            event.getPlayer().getInventory().setChanged();
            event.getPlayer().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient())
            return;
        if(event.getItemStack().getItem().equals(Items.COMPASS) && event.getItemStack().hasTag()) {
            ItemStack compass = event.getItemStack();
            if(!compass.hasTag())
                return;
            CompoundTag nbt = compass.getOrCreateTag();
            if(nbt.get("structure") == null)
                return;

            //fetch our current compass list.
            ArrayList<String> compasses = APRandomizer.getItemManager().getCompasses();

            //get our current structures index in that list, increase it by one, wrapping it to 0 if needed.
            int index = compasses.indexOf(nbt.getString("structure")) + 1;
            if(index >= compasses.size())
                index = 0;

            String structureName = compasses.get(index);

            ItemManager.updateCompassLocation(structureName,event.getPlayer(),compass);

        }
    }
}
