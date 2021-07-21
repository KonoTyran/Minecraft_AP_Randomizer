package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.structure.Structure;
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
    static void onPlayerBlockInteract(PlayerInteractEvent.RightClickBlock event) {
        if(!event.getItemStack().getItem().equals(Items.COMPASS) || !event.getItemStack().hasTag()) {
            return;
        }

        BlockState block = event.getWorld().getBlockState(event.getHitVec().getBlockPos());
        if(event.getItemStack().getTag().get("structure") != null && block.is(Blocks.LODESTONE))
            event.setCanceled(true);

        event.getPlayer().getServer().execute(() -> {
            event.getPlayer().inventory.setChanged();
            event.getPlayer().inventoryMenu.broadcastChanges();
        });
    }

    @SubscribeEvent
    static void onPlayerInteractEvent(PlayerInteractEvent.RightClickItem event) {
        if(event.getSide().isClient())
            return;
        if(event.getItemStack().getItem().equals(Items.COMPASS) && event.getItemStack().hasTag()) {
            ItemStack compass = event.getItemStack();
            CompoundNBT nbt = compass.getOrCreateTag();
            if(nbt.get("structure") == null)
                return;

            //fetch our current compass list.
            ArrayList<String> compasses = APRandomizer.getItemManager().getCompasses();

            //get our current structures index in that list, increase it by one, wrapping it to 0 if needed.
            int index = compasses.indexOf(nbt.getString("structure")) + 1;
            if(index >= compasses.size())
                index = 0;

            String structureName = compasses.get(index);

            //update the nbt data with our new structure.
            nbt.put("structure", StringNBT.valueOf(structureName));

            //get the actual structure data from forge, and make sure its changed to the AP one if needed.
            Structure<?> structure = Utils.getCorrectStructure(ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structureName)));


            //get our local custom structure if needed.
            RegistryKey<World> world = Utils.getStructureWorld(structure);

            //locate the structure in the appropriate world.
            BlockPos structurePos = APRandomizer.getServer().getLevel(world).findNearestMapFeature(structure,event.getEntity().blockPosition(), 100, false);

            String displayName = Utils.getAPStructureName(structure);

            if(structurePos == null)
                structurePos = new BlockPos(0,0,0);

            Utils.addLodestoneTags(world,structurePos,compass.getOrCreateTag());
            Utils.sendActionBarToPlayer((ServerPlayerEntity)event.getPlayer(),String.format("Updated Compass (%s)",displayName),0,60,0);
            compass.setHoverName(new StringTextComponent(String.format("Structure Compass (%s)", displayName)));

        }
    }
}
