package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod.EventBusSubscriber
public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<Long, LinkedList<PermanentInterface>> progressiveItems = new HashMap<>() {{
        put(42000L, new LinkedList<>(){{ //progressive pick
            add(new PermanentItem(new ItemStack(Items.WOODEN_PICKAXE),"pick"));
            add(new PermanentItem(new ItemStack(Items.STONE_PICKAXE), "pick"));
            add(new PermanentItem(new ItemStack(Items.IRON_PICKAXE), "pick"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_PICKAXE), "pick"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_PICKAXE), "pick"));
        }} );
        put(42001L,new LinkedList<>(){{ //progressive shovel
            add(new PermanentItem(new ItemStack(Items.WOODEN_SHOVEL),"shovel"));
            add(new PermanentItem(new ItemStack(Items.STONE_SHOVEL), "shovel"));
            add(new PermanentItem(new ItemStack(Items.IRON_SHOVEL), "shovel"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_SHOVEL), "shovel"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_SHOVEL), "shovel"));
        }} );
        put(42002L,new LinkedList<>(){{ //progressive axe
            add(new PermanentItem(new ItemStack(Items.WOODEN_AXE),"axe"));
            add(new PermanentItem(new ItemStack(Items.STONE_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.IRON_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_AXE), "axe"));
        }} );
        put(42003L,new LinkedList<>(){{ //progressive hoe
            add(new PermanentItem(new ItemStack(Items.WOODEN_HOE),"hoe"));
            add(new PermanentItem(new ItemStack(Items.STONE_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.IRON_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_HOE), "hoe"));
        }} );

    }};

    private final HashMap<Long, ItemStack> itemStacks = new HashMap<>() {{
        put(42000L, new ItemStack(Items.TNT, 16));

    }};

    private final HashMap<Long, Trap> trapData = new HashMap<>() {{
        put(42001L, new BeeTrap(3));
        put(42002L, new CreeperTrap(1));
        put(42003L, new SandRain());
        put(42004L, new FakeWither());
    }};

    private ArrayList<Long> receivedItems = new ArrayList<>();

    private static final HashMap<String, ItemStack> permanentItems = new HashMap<>();

    private void addLore(ItemStack iStack, String name, String[] Lore) {
        iStack.setHoverName(Component.literal(name));
        CompoundTag compoundNBT = iStack.getOrCreateTagElement("display");
        ListTag itemLoreLines = new ListTag();
        for (String s : Lore) {
            StringTag itemLore = StringTag.valueOf(Component.Serializer.toJson(Component.literal(s)));
            itemLoreLines.add(itemLore);
        }
        compoundNBT.put("Lore", itemLoreLines);
    }

    public void setReceivedItems(ArrayList<Long> items) {
        this.receivedItems = items;
        APRandomizer.getGoalManager().updateGoal(false);
    }

    public void giveItem(Long itemID, ServerPlayer player) {
        if (APRandomizer.isJailPlayers()) {
            //dont send items to players if game has not started.
            return;
        }
        //update the player's index of received items for syncing later.
        LazyOptional<PlayerData> loPlayerData = player.getCapability(APCapabilities.PLAYER_INDEX);
        if (loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);
            playerData.setIndex(receivedItems.size());
        }

        if (itemStacks.containsKey(itemID)) {
            ItemStack itemstack = itemStacks.get(itemID).copy();
            Utils.giveItemToPlayer(player, itemstack);
        } else if (trapData.containsKey(itemID)) {
            trapData.get(itemID).trigger(player);
        }
    }


    public void giveItemToAll(long itemID) {

        receivedItems.add(itemID);
        //check if this item is a structure compass, and we are not already tracking that one.

        APRandomizer.getServer().execute(() -> {
            for (ServerPlayer serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                giveItem(itemID, serverplayerentity);
            }
        });

        APRandomizer.getGoalManager().updateGoal(true);
    }

    /***
     fetches the index form the player's capability then makes sure they have all items after that index.
     * @param player ServerPlayer to catch up
     */
    public void catchUpPlayer(ServerPlayer player) {
        LazyOptional<PlayerData> loPlayerData = player.getCapability(APCapabilities.PLAYER_INDEX);
        if (loPlayerData.isPresent()) {
            PlayerData playerData = loPlayerData.orElseThrow(AssertionError::new);

            for (int i = playerData.getIndex(); i < receivedItems.size(); i++) {
                giveItem(receivedItems.get(i), player);
            }
        }
    }

    public ArrayList<Long> getAllItems() {
        return receivedItems;
    }

    public static void updateItem(ItemStack item, String key) {
        permanentItems.put(key,item);
    }

    @SubscribeEvent
    public static void onGameTick(TickEvent.ServerTickEvent event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            Set<String> foundItems = new HashSet<>();
            for (ItemStack item : player.getInventory().items) {
                if(item.isEmpty())
                    continue;
                String key = item.getOrCreateTag().getString("key");
                if(key.isBlank())
                   continue;

                if(!item.sameItem(permanentItems.get(key)))
                    player.getInventory().removeItem(item);
                player.getInventory().add(permanentItems.get(key));
                foundItems.add(key);
            }
            for (String key : permanentItems.keySet()) {
                if (!foundItems.contains(key))
                    player.getInventory().add(permanentItems.get(key));
            }
        }
    }
}
