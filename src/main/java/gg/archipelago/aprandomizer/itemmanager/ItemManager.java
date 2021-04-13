package gg.archipelago.aprandomizer.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private HashMap<Integer, ItemStack> itemData = new HashMap<Integer, ItemStack>()
    {{
        put(45015, new ItemStack(Items.NETHERITE_SCRAP,8));
        put(45016, new ItemStack(Items.EMERALD,8));
        put(45017, new ItemStack(Items.EMERALD,4));
        put(45018, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45019, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45020, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45021, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45022, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45023, new ItemStack(Items.ENCHANTED_BOOK,1));
        put(45024, new ItemStack(Items.DIAMOND_ORE,4));
        put(45025, new ItemStack(Items.IRON_ORE,16));
    }};

    private HashMap<Integer, Integer> xpData = new HashMap<Integer, Integer>()
    {{
        put(45026, 500);
        put(45027, 100);
        put(45028, 50);
    }};

    public ItemManager() {
        EnchantedBookItem.addEnchantment(itemData.get(45018), new EnchantmentData(Enchantments.CHANNELING,1));
        EnchantedBookItem.addEnchantment(itemData.get(45019), new EnchantmentData(Enchantments.SILK_TOUCH,1));
        EnchantedBookItem.addEnchantment(itemData.get(45020), new EnchantmentData(Enchantments.SHARPNESS,3));
        EnchantedBookItem.addEnchantment(itemData.get(45021), new EnchantmentData(Enchantments.PIERCING,4));
        EnchantedBookItem.addEnchantment(itemData.get(45022), new EnchantmentData(Enchantments.MOB_LOOTING,2));
        EnchantedBookItem.addEnchantment(itemData.get(45023), new EnchantmentData(Enchantments.INFINITY_ARROWS,1));
    }


    public void giveItem(int itemID) {
        if(itemData.containsKey(itemID)) {
            ItemStack itemStack = itemData.get(itemID);
            for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                player.inventory.add(itemStack);
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
