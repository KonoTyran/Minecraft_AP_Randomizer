package gg.archipelago.aprandomizer.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<Integer, ItemInfo> itemData = new HashMap<Integer, ItemInfo>()
    {{
        put(45015, new ItemInfo(Items.NETHERITE_SCRAP, 8, null));
        put(45016, new ItemInfo(Items.EMERALD, 8, null));
        put(45017, new ItemInfo(Items.EMERALD, 4, null));
        put(45018, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.CHANNELING,1)));
        put(45019, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SILK_TOUCH,1)));
        put(45020, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.SHARPNESS,3)));
        put(45021, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.PIERCING,4)));
        put(45022, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.MOB_LOOTING,3)));
        put(45023, new ItemInfo(Items.ENCHANTED_BOOK, 1, new EnchantmentData(Enchantments.INFINITY_ARROWS,1)));
        put(45024, new ItemInfo(Items.DIAMOND_ORE, 4, null));
        put(45025, new ItemInfo(Items.IRON_ORE, 16, null));
        put(45029, new ItemInfo(Items.ENDER_PEARL, 3, null));
        put(45004, new ItemInfo(Items.LAPIS_LAZULI, 4, null));
        put(45030, new ItemInfo(Items.LAPIS_LAZULI, 4, null));
        put(45031, new ItemInfo(Items.COOKED_PORKCHOP, 16, null));
    }};

    private final HashMap<Integer, Integer> xpData = new HashMap<Integer, Integer>()
    {{
        put(45026, 500);
        put(45027, 100);
        put(45028, 50);
    }};

    private ItemStack buildNewItemStack(int itemID) {
        ItemInfo iInfo = itemData.get(itemID);
        ItemStack iStack = new ItemStack(iInfo.item,iInfo.amount);
        if (iInfo.enchant != null) {
            EnchantedBookItem.addEnchantment(iStack, iInfo.enchant);
        }
        return iStack;
    }


    public void giveItem(int itemID) {
        if(itemData.containsKey(itemID)) {
            for (ServerPlayerEntity serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                ItemStack itemstack = buildNewItemStack(itemID);
                boolean flag = serverplayerentity.inventory.add(itemstack);
                if (flag && itemstack.isEmpty()) {
                    itemstack.setCount(1);
                    ItemEntity itementity1 = serverplayerentity.drop(itemstack, false);
                    if (itementity1 != null) {
                        itementity1.makeFakeItem();
                    }

                    serverplayerentity.level.playSound(null, serverplayerentity.getX(), serverplayerentity.getY(), serverplayerentity.getZ(), SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((serverplayerentity.getRandom().nextFloat() - serverplayerentity.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    serverplayerentity.inventoryMenu.broadcastChanges();
                }
                else {
                    ItemEntity itementity = serverplayerentity.drop(itemstack, false);
                    if (itementity != null) {
                        itementity.setNoPickUpDelay();
                        itementity.setOwner(serverplayerentity.getUUID());
                    }
                }
            }
        }
        else if (xpData.containsKey(itemID)) {
            int xpValue = xpData.get(itemID);
            for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                player.giveExperiencePoints(xpValue);
            }
        }
    }
}
