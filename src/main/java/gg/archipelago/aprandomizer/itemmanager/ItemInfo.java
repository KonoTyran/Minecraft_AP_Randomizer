package gg.archipelago.aprandomizer.itemmanager;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;

public class ItemInfo {

    Item item;
    String name;
    int amount;
    EnchantmentData enchant;

    public ItemInfo(Item item, int amount, EnchantmentData enchant, String name) {
        this.item = item;
        this.amount = amount;
        this.enchant = enchant;
        this.name = name;
    }

    public ItemInfo(Item item, int amount, EnchantmentData enchant) {
        this(item, amount, enchant, null);
    }

    public ItemInfo(Item item, int amount) {
        this(item, amount, null, null);
    }

    public ItemInfo(Item item, int amount, String name) {
        this(item, amount, null, name);
    }
}
