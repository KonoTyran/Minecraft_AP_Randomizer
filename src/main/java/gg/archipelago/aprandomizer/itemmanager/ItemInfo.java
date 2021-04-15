package gg.archipelago.aprandomizer.itemmanager;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;

public class ItemInfo {

    Item item;
    int amount;
    EnchantmentData enchant;

    public ItemInfo(Item item, int amount, EnchantmentData enchant) {
        this.item = item;
        this.amount = amount;
        this.enchant = enchant;
    }
}
