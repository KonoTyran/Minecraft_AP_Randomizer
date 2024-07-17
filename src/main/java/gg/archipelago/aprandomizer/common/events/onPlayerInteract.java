package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
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
        if(APRandomizer.isJailPlayers())
            event.setCanceled(true);
    }

    @SubscribeEvent
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if(event.getSide().isClient())
            return;
        if(!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().has(DataComponents.CUSTOM_DATA)) {
            return;
        }

        BlockState block = event.getLevel().getBlockState(event.getHitVec().getBlockPos());
        if(event.getItemStack().has(DataComponents.CUSTOM_DATA) && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getEntity().getServer().execute(() -> {
            event.getEntity().getInventory().setChanged();
            event.getEntity().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient())
            return;
        //TODO: fix compasses right click to toggle
//        if(event.getItemStack().getItem().equals(Items.COMPASS) && event.getItemStack().hasTag()) {
//            ItemStack compass = event.getItemStack();
//            if(!compass.hasTag())
//                return;
//            CompoundTag nbt = compass.getOrCreateTag();
//            if(nbt.get("structure") == null)
//                return;
//
//            //fetch our current compass list.
//            ArrayList<TagKey<Structure>> compasses = APRandomizer.getItemManager().getCompasses();
//
//            TagKey<Structure> tagKey = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse(nbt.getString("structure")));
//            //get our current structures index in that list, increase it by one, wrapping it to 0 if needed.
//            int index = compasses.indexOf(tagKey) + 1;
//            if(index >= compasses.size())
//                index = 0;
//
//            TagKey<Structure> structure = compasses.get(index);
//
//            ItemManager.updateCompassLocation(structure,event.getEntity(),compass);
//
//        }
    }
}
