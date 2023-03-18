package gg.archipelago.aprandomizer;

import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;

public class SlotData {

    public long minecraft_world_seed;
    public int client_version;

    transient public ArrayList<ItemStack> startingItemStacks = new ArrayList<>();

    public int getClient_version() {
        return client_version;
    }

    public long getMinecraft_world_seed() {
        return minecraft_world_seed;
    }
}
