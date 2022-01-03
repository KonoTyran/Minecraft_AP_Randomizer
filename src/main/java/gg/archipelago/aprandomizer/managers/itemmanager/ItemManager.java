package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.BeeTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int DRAGON_EGG_SHARD = 45043;

    private final HashMap<Integer, ItemStack> itemStacks = new HashMap<>() {{

        put(45015, new ItemStack(Items.NETHERITE_SCRAP, 8));
        put(45016, new ItemStack(Items.EMERALD, 8));
        put(45017, new ItemStack(Items.EMERALD, 4));

        ItemStack channelingBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(channelingBook,new EnchantmentInstance(Enchantments.CHANNELING, 1));
        put(45018, channelingBook);

        ItemStack silkTouchBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(silkTouchBook,new EnchantmentInstance(Enchantments.SILK_TOUCH, 1));
        put(45019, silkTouchBook);

        ItemStack sharpnessBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(sharpnessBook,new EnchantmentInstance(Enchantments.SHARPNESS, 3));
        put(45020, sharpnessBook);

        ItemStack piercingBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(piercingBook,new EnchantmentInstance(Enchantments.PIERCING, 4));
        put(45021, piercingBook);

        ItemStack lootingBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(lootingBook,new EnchantmentInstance(Enchantments.MOB_LOOTING, 3));
        put(45022, lootingBook);

        ItemStack infinityBook = new ItemStack(Items.ENCHANTED_BOOK);
        EnchantedBookItem.addEnchantment(infinityBook,new EnchantmentInstance(Enchantments.INFINITY_ARROWS, 1));
        put(45023, infinityBook);

        put(45024, new ItemStack(Items.DIAMOND_ORE, 4));
        put(45025, new ItemStack(Items.IRON_ORE, 16));
        put(45029, new ItemStack(Items.ENDER_PEARL, 3));
        put(45004, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45030, new ItemStack(Items.LAPIS_LAZULI, 4));
        put(45031, new ItemStack(Items.COOKED_PORKCHOP, 16));
        put(45032, new ItemStack(Items.GOLD_ORE, 8));
        put(45033, new ItemStack(Items.ROTTEN_FLESH, 8));
        put(45034, new ItemStack(Items.ARROW, 1).setHoverName(new TextComponent("The Arrow")));
        put(45035, new ItemStack(Items.ARROW, 32));
        put(45036, new ItemStack(Items.SADDLE, 1));

        String[] compassLore = new String[]{"Right click with compass in hand to","cycle to next known structure location."};

        ItemStack villageCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(villageCompass, Utils.getCorrectStructure(StructureFeature.VILLAGE));
        addLore(villageCompass, "Structure Compass (Village)", compassLore);
        put(45037, villageCompass);

        ItemStack outpostCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(outpostCompass, Utils.getCorrectStructure(StructureFeature.PILLAGER_OUTPOST));
        addLore(outpostCompass, "Structure Compass (Pillager Outpost)", compassLore);
        put(45038, outpostCompass);

        ItemStack fortressCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(fortressCompass, Utils.getCorrectStructure(StructureFeature.NETHER_BRIDGE));
        addLore(fortressCompass, "Structure Compass (Nether Fortress)", compassLore);
        put(45039, fortressCompass);

        ItemStack bastionCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(bastionCompass, Utils.getCorrectStructure(StructureFeature.BASTION_REMNANT));
        addLore(bastionCompass, "Structure Compass (Bastion Remnant)", compassLore);
        put(45040,bastionCompass);

        ItemStack endCityCompass = new ItemStack(Items.COMPASS, 1);
        makeCompass(endCityCompass, Utils.getCorrectStructure(StructureFeature.END_CITY));
        addLore(endCityCompass, "Structure Compass (End City)", compassLore);
        put(45041, endCityCompass);

        put(45042, new ItemStack(Items.SHULKER_BOX, 1));
    }};

    private final HashMap<Integer,String> compasses = new HashMap<>() {{
        put(45037, StructureFeature.VILLAGE.getFeatureName());
        put(45038, StructureFeature.PILLAGER_OUTPOST.getFeatureName());
        put(45039, StructureFeature.NETHER_BRIDGE.getFeatureName());
        put(45040, StructureFeature.BASTION_REMNANT.getFeatureName());
        put(45041, StructureFeature.END_CITY.getFeatureName());

    }};

    private final HashMap<Integer, Integer> xpData = new HashMap<>() {{
        put(45026, 500);
        put(45027, 100);
        put(45028, 50);
    }};

    private final HashMap<Integer, Trap> trapData = new HashMap<>() {{
        put(45100, new BeeTrap(3));
    }};

    private ArrayList<Integer> receivedItems = new ArrayList<>();

    private final ArrayList<String> receivedCompasses = new ArrayList<>();

    private void makeCompass(ItemStack iStack, StructureFeature<?> structure) {
        CompoundTag nbt = iStack.getOrCreateTag();
        nbt.put("structure", StringTag.valueOf(structure.getRegistryName().toString()));

        BlockPos structureCords = new BlockPos(0,0,0);

        Utils.addLodestoneTags(Utils.getStructureWorld(structure),structureCords, iStack.getOrCreateTag());
    }

    private void addLore(ItemStack iStack, String name, String[] compassLore) {
        iStack.setHoverName(new TextComponent(name));
        CompoundTag compoundnbt = iStack.getOrCreateTagElement("display");
        ListTag itemLoreLines = new ListTag();
        for (String s : compassLore) {
            StringTag itemLore = StringTag.valueOf(Component.Serializer.toJson(new TextComponent(s)));
            itemLoreLines.add(itemLore);
        }
        compoundnbt.put("Lore",itemLoreLines);
    }

    public void setReceivedItems(ArrayList<Integer> items) {
        this.receivedItems = items;
        for (Integer item : items) {
            if(compasses.containsKey(item) && !receivedCompasses.contains(compasses.get(item))) {
                receivedCompasses.add(compasses.get(item));
            }
        }
        APRandomizer.getGoalManager().updateGoal(false);
    }

    public void giveItem(int itemID, ServerPlayer player) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }
        //update the player's index of received items for syncing later.
        LazyOptional<PlayerData> loPlayerData = player.getCapability(APCapabilities.PLAYER_INDEX);
        if (loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);
            playerData.setIndex(receivedItems.size());
        }

        if (itemStacks.containsKey(itemID)) {
            ItemStack itemstack = itemStacks.get(itemID).copy();
            if(compasses.containsKey(itemID)){
                updateCompassLocation(itemstack.getOrCreateTag().getString("structure"), player , itemstack);
            }
            Utils.giveItemToPlayer(player, itemstack);
        } else if (xpData.containsKey(itemID)) {
            int xpValue = xpData.get(itemID);
            player.giveExperiencePoints(xpValue);
        } else if (trapData.containsKey(itemID)) {
            trapData.get(itemID).trigger(player);
        }
    }


    public void giveItemToAll(int itemID) {

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
        LazyOptional<PlayerData> loPlayerData = player.getCapability(APCapabilities.PLAYER_INDEX);
        if (loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);

            for (int i = playerData.getIndex(); i < receivedItems.size(); i++) {
                giveItem(receivedItems.get(i), player);
            }
        }
    }

    public ArrayList<String> getCompasses() {
        return receivedCompasses;
    }

    public ArrayList<Integer> getAllItems() {
        return receivedItems;
    }

    public static void updateCompassLocation(String structureName, Player player, ItemStack compass) {

        //get the actual structure data from forge, and make sure its changed to the AP one if needed.
        StructureFeature<?> structure = Utils.getCorrectStructure(ForgeRegistries.STRUCTURE_FEATURES.getValue(new ResourceLocation(structureName)));
        //get our local custom structure if needed.
        ResourceKey<Level> world = Utils.getStructureWorld(structure);

        //only locate structure if the player is in the same world as the one for the compass
        //otherwise just point it to 0,0 in said dimension.
        BlockPos structurePos = new BlockPos(0,0,0);
        if(player.getCommandSenderWorld().dimension().equals(world)) {
            structurePos = APRandomizer.getServer().getLevel(world).findNearestMapFeature(structure, player.blockPosition(), 75, false);
        }

        String displayName = Utils.getAPStructureName(structure);

        if(structurePos == null)
            structurePos = new BlockPos(0,0,0);

        CompoundTag nbt = compass.getOrCreateTag();
        //update the nbt data with our new structure.
        nbt.put("structure", StringTag.valueOf(structureName));
        Utils.addLodestoneTags(world,structurePos,nbt);
        compass.setHoverName(new TextComponent(String.format("Structure Compass (%s)", displayName)));
    }
}
