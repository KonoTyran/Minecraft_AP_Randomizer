package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.BeeTrap;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public static final int DRAGON_EGG_SHARD = 45043;

    private final HashMap<Integer, ItemInfo> itemData = new HashMap<Integer, ItemInfo>() {{
        put(45015, new ItemInfo(Items.NETHERITE_SCRAP, 8));
        put(45016, new ItemInfo(Items.EMERALD, 8));
        put(45017, new ItemInfo(Items.EMERALD, 4));
        put(45018, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.CHANNELING, 1)));
        put(45019, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.SILK_TOUCH, 1)));
        put(45020, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.SHARPNESS, 3)));
        put(45021, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.PIERCING, 4)));
        put(45022, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.MOB_LOOTING, 3)));
        put(45023, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentInstance(Enchantments.INFINITY_ARROWS, 1)));
        put(45024, new ItemInfo(Items.DIAMOND_ORE, 4));
        put(45025, new ItemInfo(Items.IRON_ORE, 16));
        put(45029, new ItemInfo(Items.ENDER_PEARL, 3));
        put(45004, new ItemInfo(Items.LAPIS_LAZULI, 4));
        put(45030, new ItemInfo(Items.LAPIS_LAZULI, 4));
        put(45031, new ItemInfo(Items.COOKED_PORKCHOP, 16));
        put(45032, new ItemInfo(Items.GOLD_ORE, 8));
        put(45033, new ItemInfo(Items.ROTTEN_FLESH, 8));
        put(45034, new ItemInfo(Items.ARROW, 1, "The Arrow"));
        put(45035, new ItemInfo(Items.ARROW, 32));
        put(45036, new ItemInfo(Items.SADDLE, 1));
        put(45037, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(StructureFeature.VILLAGE), "Structure Compass (Village)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45038, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(StructureFeature.PILLAGER_OUTPOST), "Structure Compass (Pillager Outpost)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45039, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(StructureFeature.NETHER_BRIDGE), "Structure Compass (Nether Fortress)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45040, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(StructureFeature.BASTION_REMNANT), "Structure Compass (Bastion Remnant)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45041, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(StructureFeature.END_CITY), "Structure Compass (End City)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45042, new ItemInfo(Items.SHULKER_BOX, 1));
    }};

    private final HashMap<Integer,String> compasses = new HashMap<Integer,String>() {{
        put(45037, StructureFeature.VILLAGE.getFeatureName());
        put(45038, StructureFeature.PILLAGER_OUTPOST.getFeatureName());
        put(45039, StructureFeature.NETHER_BRIDGE.getFeatureName());
        put(45040, StructureFeature.BASTION_REMNANT.getFeatureName());
        put(45041, StructureFeature.END_CITY.getFeatureName());

    }};

    private final HashMap<Integer, Integer> xpData = new HashMap<Integer, Integer>() {{
        put(45026, 500);
        put(45027, 100);
        put(45028, 50);
    }};

    private final HashMap<Integer, Trap> trapData = new HashMap<Integer, Trap>() {{
        put(45100, new BeeTrap(3));
    }};

    private ArrayList<Integer> receivedItems = new ArrayList<>();

    private final ArrayList<String> receivedCompasses = new ArrayList<>();

    private ItemStack buildNewItemStack(int itemID) {
        ItemInfo iInfo = itemData.get(itemID);
        ItemStack iStack = new ItemStack(iInfo.item, iInfo.amount);
        if (iInfo.enchant != null) {
            EnchantedBookItem.addEnchantment(iStack, iInfo.enchant);
        }
        if (iInfo.name != null) {
            iStack.setHoverName(new TextComponent(iInfo.name));
        }
        if (iInfo.lore != null) {
            CompoundTag compoundnbt = iStack.getOrCreateTagElement("display");
            ListTag itemLoreLines = new ListTag();
            for (String s : iInfo.lore) {
                StringTag itemLore = StringTag.valueOf(Component.Serializer.toJson(new TextComponent(s)));
                itemLoreLines.add(itemLore);
            }
            compoundnbt.put("Lore",itemLoreLines);
        }
        if (iInfo.structure != null && iStack.getItem().equals(Items.COMPASS)) {
            CompoundTag nbt = iStack.getOrCreateTag();
            nbt.put("structure", StringTag.valueOf(iInfo.structure.getRegistryName().toString()));

            BlockPos structureCords = APRandomizer.getServer().getLevel(Utils.getStructureWorld(iInfo.structure)).findNearestMapFeature(iInfo.structure, new BlockPos(0,0,0), 100, false);

            Utils.addLodestoneTags(Utils.getStructureWorld(iInfo.structure),structureCords, iStack.getOrCreateTag());

        }
        return iStack;
    }

    public void setReceivedItems(ArrayList<Integer> items) {
        this.receivedItems = items;
        for (Integer item : items) {
            if(compasses.containsKey(item) && !receivedCompasses.contains(compasses.get(item))) {
                receivedCompasses.add(compasses.get(item));
            }
        }
        APRandomizer.getGoalManager().updateGoal();
    }

    public void giveItem(int itemID, ServerPlayer player) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }
        //update the player's index of received items for syncing later.
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if (loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);
            playerData.setIndex(receivedItems.size());
        }

        if (itemData.containsKey(itemID)) {
            ItemStack itemstack = buildNewItemStack(itemID);
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

        APRandomizer.getGoalManager().updateGoal();
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
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
}
