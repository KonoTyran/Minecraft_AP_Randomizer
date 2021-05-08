package gg.archipelago.aprandomizer.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.capability.PlayerData;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<Integer, ItemInfo> itemData = new HashMap<Integer, ItemInfo>()
    {{
        put(45015, new ItemInfo(Items.NETHERITE_SCRAP, 8));
        put(45016, new ItemInfo(Items.EMERALD, 8));
        put(45017, new ItemInfo(Items.EMERALD, 4));
        put(45018, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.CHANNELING,1)));
        put(45019, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SILK_TOUCH,1)));
        put(45020, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SHARPNESS,3)));
        put(45021, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.PIERCING,4)));
        put(45022, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.MOB_LOOTING,3)));
        put(45023, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.INFINITY_ARROWS,1)));
        put(45024, new ItemInfo(Items.DIAMOND_ORE, 4));
        put(45025, new ItemInfo(Items.IRON_ORE, 16));
        put(45029, new ItemInfo(Items.ENDER_PEARL, 3));
        put(45004, new ItemInfo(Items.LAPIS_LAZULI, 4));
        put(45030, new ItemInfo(Items.LAPIS_LAZULI, 4));
        put(45031, new ItemInfo(Items.COOKED_PORKCHOP, 16));
        put(45032, new ItemInfo(Items.GOLD_ORE, 8));
        put(45033, new ItemInfo(Items.ROTTEN_FLESH, 8));
        put(45034, new ItemInfo(Items.ARROW, 1,"The Arrow"));
    }};

    private final HashMap<Integer, Integer> xpData = new HashMap<Integer, Integer>()
    {{
        put(45026, 500);
        put(45027, 100);
        put(45028, 50);
    }};

    private ArrayList<Integer> receivedItems = new ArrayList<>();

    private ItemStack buildNewItemStack(int itemID) {
        ItemInfo iInfo = itemData.get(itemID);
        ItemStack iStack = new ItemStack(iInfo.item,iInfo.amount);
        if (iInfo.enchant != null) {
            EnchantedBookItem.addEnchantment(iStack, iInfo.enchant);
        }
        if(iInfo.name != null) {
            iStack.setHoverName(new StringTextComponent(iInfo.name));
        }
        return iStack;
    }

    public void setReceivedItems (ArrayList<Integer> items) {
        this.receivedItems = items;
    }

    public void giveItem(int itemID, ServerPlayerEntity player) {

        //update the player's index of received items for syncing later.
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if(loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);
            playerData.setIndex(receivedItems.size());
        }

        if(itemData.containsKey(itemID)) {
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
            }
            else {
                ItemEntity itementity = player.drop(itemstack, false);
                if (itementity != null) {
                    itementity.setNoPickUpDelay();
                    itementity.setOwner(player.getUUID());
                }
            }
        }
        else if (xpData.containsKey(itemID)) {
            int xpValue = xpData.get(itemID);
            player.giveExperiencePoints(xpValue);
        }
    }


    public void giveItemToAll(int itemID) {
        receivedItems.add(itemID);
        for (ServerPlayerEntity serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
            giveItem(itemID, serverplayerentity);
        }
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayerEntity player) {
        LazyOptional<PlayerData> loPlayerData = player.getCapability(CapabilityPlayerData.CAPABILITY_PLAYER_DATA);
        if(loPlayerData.isPresent()) {
            PlayerData playerData= loPlayerData.orElseThrow(AssertionError::new);

            for (int i = playerData.getIndex(); i < receivedItems.size(); i++) {
                giveItem(receivedItems.get(i),player);
            }
        }
    }
}
