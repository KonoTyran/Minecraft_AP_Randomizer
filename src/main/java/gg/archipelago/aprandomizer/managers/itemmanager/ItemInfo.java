package gg.archipelago.aprandomizer.managers.itemmanager;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.Item;
import net.minecraft.world.gen.feature.structure.Structure;

public class ItemInfo {

    Item item;
    String name;
    String[] lore;
    int amount;
    EnchantmentData enchant;
    Structure<?> structure;

    public ItemInfo(Item item, int amount, EnchantmentData enchant, Structure<?> structure, String name, String[] lore) {
        this.item = item;
        this.amount = amount;
        this.enchant = enchant;
        this.name = name;
        this.structure = structure;
        this.lore = lore;
    }

    public ItemInfo(Item item, int amount, EnchantmentData enchant) {
        this(item, amount, enchant, null, null, null);
    }

    public ItemInfo(Item item, int amount) {
        this(item, amount, null, null, null, null);
    }

    public ItemInfo(Item item, int amount, String name) {
        this(item, amount, null, null, name, null);
    }

    public ItemInfo(Item item, int amount, String name, String[] lore) {
        this(item, amount, null, null, name, lore);
    }

    public ItemInfo(Item item, int amount, Structure<?> structure, String name, String[] lore) {
        this(item, amount, null, structure, name, lore);
    }
}
