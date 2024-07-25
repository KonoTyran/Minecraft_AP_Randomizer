package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStructures;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.*;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.Callable;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final long DRAGON_EGG_SHARD = 45043L;

    private final HashMap<Long, ItemStack> itemStacks = new HashMap<>() {{
        var enchantmentRegistry = APRandomizer.getServer().registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        put(45015L, new ItemStack(Items.NETHERITE_SCRAP, 8));
        put(45016L, new ItemStack(Items.EMERALD, 8));
        put(45017L, new ItemStack(Items.EMERALD, 4));

        enchantmentRegistry.getHolder(Enchantments.CHANNELING).ifPresent(
                ref -> put(45018L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 1)))
        );

        enchantmentRegistry.getHolder(Enchantments.SILK_TOUCH).ifPresent(
                ref -> put(45019L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 1)))
        );

        enchantmentRegistry.getHolder(Enchantments.SHARPNESS).ifPresent(
                ref -> put(45020L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 3)))
        );

        enchantmentRegistry.getHolder(Enchantments.PIERCING).ifPresent(
                ref -> put(45021L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 4)))
        );

        enchantmentRegistry.getHolder(Enchantments.LOOTING).ifPresent(
                ref -> put(45022L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 3)))
        );

        enchantmentRegistry.getHolder(Enchantments.INFINITY).ifPresent(
                ref -> put(45023L, EnchantedBookItem.createForEnchantment(new EnchantmentInstance(ref, 1)))
    );

        put(45024L, new ItemStack(Items.DIAMOND_ORE, 4));
        put(45025L, new ItemStack(Items.IRON_ORE, 16));
        put(45029L, new ItemStack(Items.ENDER_PEARL, 3));
        put(45004L, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45030L, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45031L, new ItemStack(Items.COOKED_PORKCHOP, 16));
        put(45032L, new ItemStack(Items.GOLD_ORE, 8));
        put(45033L, new ItemStack(Items.ROTTEN_FLESH, 8));
        put(45034L, new ItemStack(Items.ARROW, 1));
        put(45035L, new ItemStack(Items.ARROW, 32));
        put(45036L, new ItemStack(Items.SADDLE, 1));

        String[] compassLore = new String[]{"Right click with compass in hand to","cycle to next known structure location."};

        ItemStack villageCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(villageCompass, APStructures.VILLAGE_TAG);
        addLore(villageCompass, "Structure Compass (Village)", compassLore);
        put(45037L, villageCompass);

        ItemStack outpostCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(outpostCompass, APStructures.OUTPOST_TAG);
        addLore(outpostCompass, "Structure Compass (Pillager Outpost)", compassLore);
        put(45038L, outpostCompass);

        ItemStack fortressCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(fortressCompass, APStructures.FORTRESS_TAG);
        addLore(fortressCompass, "Structure Compass (Nether Fortress)", compassLore);
        put(45039L, fortressCompass);

        ItemStack bastionCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(bastionCompass, APStructures.BASTION_REMNANT_TAG);
        addLore(bastionCompass, "Structure Compass (Bastion Remnant)", compassLore);
        put(45040L,bastionCompass);

        ItemStack endCityCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(endCityCompass, APStructures.END_CITY_TAG);
        addLore(endCityCompass, "Structure Compass (End City)", compassLore);
        put(45041L, endCityCompass);

        put(45042L, new ItemStack(Items.SHULKER_BOX, 1));
    }};

    private final HashMap<Long,TagKey<Structure>> compasses = new HashMap<>() {{
        put(45037L, APStructures.VILLAGE_TAG);
        put(45038L, APStructures.OUTPOST_TAG);
        put(45039L, APStructures.FORTRESS_TAG);
        put(45040L, APStructures.BASTION_REMNANT_TAG);
        put(45041L, APStructures.END_CITY_TAG);

    }};

    private final HashMap<Long, Integer> xpData = new HashMap<>() {{
        put(45026L, 500);
        put(45027L, 100);
        put(45028L, 50);
    }};

    long index = 45100L;
    private final HashMap<Long, Callable<Trap>> trapData = new HashMap<>() {{
        put(index++, BeeTrap::new);
        put(index++, CreeperTrap::new);
        put(index++, SandRain::new);
        put(index++, FakeWither::new);
        put(index++, GoonTrap::new);
        put(index++, FishFountainTrap::new);
        put(index++, MiningFatigueTrap::new);
        put(index++, BlindnessTrap::new);
        put(index++, PhantomTrap::new);
        put(index++, WaterTrap::new);
        put(index++, GhastTrap::new);
        put(index++, LevitateTrap::new);
        put(index++, AboutFaceTrap::new);
        put(index++, AnvilTrap::new);
    }};

    private ArrayList<Long> receivedItems = new ArrayList<>();

    private final ArrayList<TagKey<Structure>> receivedCompasses = new ArrayList<>();

    private void makeCompass(ItemStack iStack, TagKey<Structure> structureTag) {
        var data = iStack.getComponents().get(DataComponents.CUSTOM_DATA);

        //TODO: figure out CUSTOM_DATA
//        iStack.set(DataComponents.CUSTOM_DATA,);
//        data.put("structure", StringTag.valueOf(structureTag.location().toString()));

        BlockPos structureCords = new BlockPos(0,0,0);
        iStack.set(DataComponents.LODESTONE_TRACKER,new LodestoneTracker(Optional.of(new GlobalPos(Utils.getStructureWorld(structureTag), structureCords)),false));
    }

    private void addLore(ItemStack iStack, String name, String[] compassLore) {
        iStack.set(DataComponents.CUSTOM_NAME, Component.literal(name));

        List<Component> itemLore = new ArrayList<>();
        for (String s : compassLore) {
            itemLore.add(Component.literal(s));
        }

        iStack.set(DataComponents.LORE, new ItemLore(itemLore));
    }

    public void setReceivedItems(ArrayList<Long> items) {
        this.receivedItems = items;
        for (var item : items) {
            if(compasses.containsKey(item) && !receivedCompasses.contains(compasses.get(item))) {
                receivedCompasses.add(compasses.get(item));
            }
        }
        APRandomizer.getGoalManager().updateGoal(false);
    }

    public void giveItem(Long itemID, ServerPlayer player) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }
        //update the player's index of received items for syncing later.
        APRandomizer.getWorldData().updatePlayerIndex(player.getStringUUID(),receivedItems.size());

        if (itemStacks.containsKey(itemID)) {
            ItemStack itemstack = itemStacks.get(itemID).copy();
            if(compasses.containsKey(itemID)){
                //TODO: figure out CUSTOM_DATA
//                CustomData location = DataComponents
//
//                TagKey<Structure> tag = TagKey.create(Registries.STRUCTURE, ResourceLocation.parse());
//                updateCompassLocation(tag, player , itemstack);
            }
            Utils.giveItemToPlayer(player, itemstack);
        } else if (xpData.containsKey(itemID)) {
            int xpValue = xpData.get(itemID);
            player.giveExperiencePoints(xpValue);
        } else if (trapData.containsKey(itemID)) {
            try {
                trapData.get(itemID).call().trigger(player);
            } catch (Exception ignored) {
            }
        }
    }


    public void giveItemToAll(long itemID) {

        receivedItems.add(itemID);
        //check if this item is a structure compass, and we are not already tracking that one.
        if(compasses.containsKey(itemID) && !receivedCompasses.contains(compasses.get(itemID))) {
            receivedCompasses.add(compasses.get(itemID));
        }

        APRandomizer.getServer().execute(() -> {
            for (ServerPlayer serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                giveItem(itemID, serverplayerentity);
            }
        });

        APRandomizer.getGoalManager().updateGoal(true);
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        int playerIndex = APRandomizer.getWorldData().getPlayerIndex(player.getStringUUID());

        for (int i = playerIndex; i < receivedItems.size(); i++) {
            giveItem(receivedItems.get(i), player);
        }

    }

    public ArrayList<TagKey<Structure>> getCompasses() {
        return receivedCompasses;
    }

    public ArrayList<Long> getAllItems() {
        return receivedItems;
    }

    public static void updateCompassLocation(TagKey<Structure> structureTag, Player player, ItemStack compass) {

        //get the actual structure data from forge, and make sure its changed to the AP one if needed.

        //get our local custom structure if needed.
        ResourceKey<Level> world = Utils.getStructureWorld(structureTag);

        //only locate structure if the player is in the same world as the one for the compass
        //otherwise just point it to 0,0 in said dimension.
        BlockPos structurePos = new BlockPos(0,0,0);

        var displayName = Component.literal(String.format("Structure Compass (%s)", Utils.getAPStructureName(structureTag)));
        if(player.getCommandSenderWorld().dimension().equals(world)) {
            try {
                structurePos = APRandomizer.getServer().getLevel(world).findNearestMapStructure(structureTag, player.blockPosition(), 75, false);
            } catch (NullPointerException exception) {
                player.sendSystemMessage(Component.literal("Could not find a nearby " + Utils.getAPStructureName(structureTag)));
            }
        }
        else {
            displayName = Component.literal(
                    String.format("Structure Compass (%s) Wrong Dimension",
                            Utils.getAPStructureName(structureTag))
                    ).withStyle(ChatFormatting.DARK_RED);
        }

        if(structurePos == null)
            structurePos = new BlockPos(0,0,0);
        //update the nbt data with our new structure.

        //nbt.put("structure", StringTag.valueOf(structureTag.location().toString()));


        compass.set(DataComponents.LODESTONE_TRACKER, new LodestoneTracker(Optional.of(new GlobalPos(world,structurePos)),false));
        compass.set(DataComponents.CUSTOM_NAME, displayName);
    }
}
