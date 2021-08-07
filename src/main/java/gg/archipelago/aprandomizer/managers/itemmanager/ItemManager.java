package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.BeeTrap;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.feature.structure.Structure;
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
        put(45018, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.CHANNELING, 1)));
        put(45019, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SILK_TOUCH, 1)));
        put(45020, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SHARPNESS, 3)));
        put(45021, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.PIERCING, 4)));
        put(45022, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.MOB_LOOTING, 3)));
        put(45023, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.INFINITY_ARROWS, 1)));
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
        put(45037, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(Structure.VILLAGE), "Structure Compass (Village)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45038, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(Structure.PILLAGER_OUTPOST), "Structure Compass (Pillager Outpost)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45039, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(Structure.NETHER_BRIDGE), "Structure Compass (Nether Fortress)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45040, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(Structure.BASTION_REMNANT), "Structure Compass (Bastion Remnant)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45041, new ItemInfo(Items.COMPASS, 1, Utils.getCorrectStructure(Structure.END_CITY), "Structure Compass (End City)", new String[]{"Right click with compass in hand to","cycle to next known structure location."}));
        put(45042, new ItemInfo(Items.SHULKER_BOX, 1));
    }};

    private final HashMap<Integer,String> compasses = new HashMap<Integer,String>() {{
        put(45037, Structure.VILLAGE.getFeatureName());
        put(45038, Structure.PILLAGER_OUTPOST.getFeatureName());
        put(45039, Structure.NETHER_BRIDGE.getFeatureName());
        put(45040, Structure.BASTION_REMNANT.getFeatureName());
        put(45041, Structure.END_CITY.getFeatureName());

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
            iStack.setHoverName(new StringTextComponent(iInfo.name));
        }
        if (iInfo.lore != null) {
            CompoundNBT compoundnbt = iStack.getOrCreateTagElement("display");
            ListNBT itemLoreLines = new ListNBT();
            for (String s : iInfo.lore) {
                StringNBT itemLore = StringNBT.valueOf(ITextComponent.Serializer.toJson(new StringTextComponent(s)));
                itemLoreLines.add(itemLore);
            }
            compoundnbt.put("Lore",itemLoreLines);
        }
        if (iInfo.structure != null && iStack.getItem().equals(Items.COMPASS)) {
            CompoundNBT nbt = iStack.getOrCreateTag();
            nbt.put("structure", StringNBT.valueOf(iInfo.structure.getRegistryName().toString()));

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

    public void giveItem(int itemID, ServerPlayerEntity player) {
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
            boolean flag = player.inventory.add(itemstack);
            if (flag && itemstack.isEmpty()) {
                itemstack.setCount(1);
                ItemEntity itementity1 = player.drop(itemstack, false);
                if (itementity1 != null) {
                    itementity1.makeFakeItem();
                }

                player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRandom().nextFloat() - player.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                player.inventoryMenu.broadcastChanges();
            } else {
                ItemEntity itementity = player.drop(itemstack, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(player.getUUID());
                }
            }
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
            for (ServerPlayerEntity serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                giveItem(itemID, serverplayerentity);
            }
        });

        APRandomizer.getGoalManager().updateGoal();
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayerEntity player) {
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
