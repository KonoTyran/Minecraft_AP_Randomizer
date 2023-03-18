package gg.archipelago.aprandomizer.managers.itemmanager;

import net.minecraft.world.item.ItemStack;

public class PermanentItem implements PermanentInterface {

    ItemStack item;
    String key;

    public PermanentItem(ItemStack itemStack, String key) {
        itemStack.getOrCreateTag().putBoolean("Unbreakable", true);
        itemStack.getOrCreateTag().putString("key", key);
        this.item = itemStack;
        this.key = key;
    }

    @Override
    public void applyEffect() {
        ItemManager.updateItem(item, key);
    }
}
