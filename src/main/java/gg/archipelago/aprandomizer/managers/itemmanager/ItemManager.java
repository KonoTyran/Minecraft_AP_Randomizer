package gg.archipelago.aprandomizer.managers.itemmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.powers.ExcavationPower;
import gg.archipelago.aprandomizer.managers.itemmanager.powers.Power;
import gg.archipelago.aprandomizer.managers.itemmanager.traps.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

@Mod.EventBusSubscriber
public class ItemManager {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final HashMap<Long, ProgressiveList<PermanentInterface>> progressiveItems = new HashMap<>() {{
        put(42000L, new ProgressiveList<>() {{ //progressive pick
            add(new PermanentItem(new ItemStack(Items.IRON_PICKAXE), "pick"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_PICKAXE), "pick"));

            ItemStack eff3 = new ItemStack(Items.NETHERITE_PICKAXE);
            eff3.enchant(Enchantments.BLOCK_EFFICIENCY, 3);
            add(new PermanentItem(eff3, "pick"));

            ItemStack eff5 = new ItemStack(Items.NETHERITE_PICKAXE);
            eff5.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
            add(new PermanentItem(eff5, "pick"));

            ItemStack eff7 = new ItemStack(Items.NETHERITE_PICKAXE);
            eff7.enchant(Enchantments.BLOCK_EFFICIENCY, 7);
            add(new PermanentItem(eff7, "pick"));

            ItemStack eff9 = new ItemStack(Items.NETHERITE_PICKAXE);
            eff9.enchant(Enchantments.BLOCK_EFFICIENCY, 9);
            add(new PermanentItem(eff9, "pick"));

            ItemStack eff11 = new ItemStack(Items.NETHERITE_PICKAXE);
            eff11.enchant(Enchantments.BLOCK_EFFICIENCY, 11);
            add(new PermanentItem(eff11, "pick"));
        }});
        put(42001L, new ProgressiveList<>() {{ //progressive shovel
            add(new PermanentItem(new ItemStack(Items.IRON_SHOVEL), "shovel"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_SHOVEL), "shovel"));

            ItemStack eff3 = new ItemStack(Items.NETHERITE_SHOVEL);
            eff3.enchant(Enchantments.BLOCK_EFFICIENCY, 3);
            add(new PermanentItem(eff3, "shovel"));

            ItemStack eff5 = new ItemStack(Items.NETHERITE_SHOVEL);
            eff5.enchant(Enchantments.BLOCK_EFFICIENCY, 5);
            add(new PermanentItem(eff5, "shovel"));

            ItemStack eff7 = new ItemStack(Items.NETHERITE_SHOVEL);
            eff7.enchant(Enchantments.BLOCK_EFFICIENCY, 7);
            add(new PermanentItem(eff7, "shovel"));
        }});
        put(42002L, new ProgressiveList<>() {{ //progressive axe
            add(new PermanentItem(new ItemStack(Items.WOODEN_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.STONE_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.IRON_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_AXE), "axe"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_AXE), "axe"));
        }});
        put(42003L, new ProgressiveList<>() {{ //progressive hoe
            add(new PermanentItem(new ItemStack(Items.WOODEN_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.STONE_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.IRON_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.DIAMOND_HOE), "hoe"));
            add(new PermanentItem(new ItemStack(Items.NETHERITE_HOE), "hoe"));
        }});
        put(42004L, new ProgressiveList<>() {{ //progressive hoe
            add(new PermanentEffect(new MobEffectInstance(MobEffects.DIG_SPEED, MobEffectInstance.INFINITE_DURATION, 0, true, false), "haste"));
            add(new PermanentEffect(new MobEffectInstance(MobEffects.DIG_SPEED, MobEffectInstance.INFINITE_DURATION, 1, true, false), "haste"));
            add(new PermanentEffect(new MobEffectInstance(MobEffects.DIG_SPEED, MobEffectInstance.INFINITE_DURATION, 2, true, false), "haste"));
            add(new PermanentEffect(new MobEffectInstance(MobEffects.DIG_SPEED, MobEffectInstance.INFINITE_DURATION, 3, true, false), "haste"));
            add(new PermanentEffect(new MobEffectInstance(MobEffects.DIG_SPEED, MobEffectInstance.INFINITE_DURATION, 4, true, false), "haste"));
        }});

    }};

    private final HashMap<Long, ItemStack> itemStacks = new HashMap<>() {{
        put(42005L, new ItemStack(Items.TNT, 16));

        ItemStack goldenPick = new ItemStack(Items.GOLDEN_PICKAXE);
        goldenPick.enchant(Enchantments.BLOCK_EFFICIENCY, 10);
        goldenPick.enchant(Enchantments.UNBREAKING, 5);
        put(42006L, goldenPick);

        ItemStack trueGoldenPick = new ItemStack(Items.GOLDEN_PICKAXE);
        addLore(trueGoldenPick,"True Golden Pickaxe", new String[] {
                "A golden pickaxe,",
                "A tool of great power,",
                "But also of great danger.",
                "",
                "It can mine the most precious ores,",
                "But it can also destroy the most hardened structures.",
                "",
                "It is a tool to be used with caution,",
                "For it can be a tool of both good and evil."});
        trueGoldenPick.enchant(Enchantments.BLOCK_EFFICIENCY, 2);
        trueGoldenPick.setDamageValue(trueGoldenPick.getMaxDamage() - 1);
        trueGoldenPick.getOrCreateTag().putBoolean("truepick", true);
        put(42007L, trueGoldenPick);

        ItemStack DefensiveFish = new ItemStack(Items.SALMON);
        DefensiveFish.enchant(Enchantments.KNOCKBACK, 5);
        put(42008L, DefensiveFish);
    }};

    private final HashMap<Long, Callable<Trap>> trapData = new HashMap<>() {{
        put(42009L, BeeTrap::new);
        put(42010L, CreeperTrap::new);
        put(42011L, SandRain::new);
        put(42012L, FakeWither::new);
        put(42013L, GoonTrap::new);
        put(42014L, FishFountainTrap::new);
        put(42015L, MiningFatigueTrap::new);
        put(42016L, BlindnessTrap::new);
        put(42017L, PhantomTrap::new);
        put(42018L, WaterTrap::new);
        put(42019L, GhastTrap::new);
        put(42020L, LevitateTrap::new);
        put(42021L, AboutFaceTrap::new);
        put(42022L, AnvilTrap::new);
    }};

    private final HashMap<Long, Power> powers = new HashMap<>() {{
        put(42023L, new ExcavationPower());
    }};

    private ArrayList<Long> receivedItems = new ArrayList<>();

    private static final HashMap<String, ItemStack> permanentItems = new HashMap<>();
    private static final HashMap<String, MobEffectInstance> permanentEffects = new HashMap<>();

    public ItemManager() {
        permanentEffects.put("saturation", new MobEffectInstance(MobEffects.SATURATION, MobEffectInstance.INFINITE_DURATION, 0, true, false));
    }

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

        if (itemStacks.containsKey(itemID)) {
            ItemStack itemstack = itemStacks.get(itemID).copy();
            Utils.giveItemToPlayer(player, itemstack);
        } else if (trapData.containsKey(itemID)) {
            try {
                trapData.get(itemID).call().trigger(player);
            } catch (Exception ignored) {
            }
        }
    }


    public boolean giveItemToAll(long itemID, long index) {
        receivedItems.add(itemID);

        if (progressiveItems.containsKey(itemID)) {
            progressiveItems.get(itemID).getNext().applyEffect();
        }

        if (powers.containsKey(itemID)) {
            powers.get(itemID).grantPower();
        }

        if(APRandomizer.worldData.getIndex() <= index) {
            APRandomizer.getServer().execute(() -> {
                APRandomizer.worldData.setIndex(index);
                for (ServerPlayer serverplayerentity : APRandomizer.getServer().getPlayerList().getPlayers()) {
                    giveItem(itemID, serverplayerentity);
                }
            });
            return true;
        }
        return false;
    }

    public ArrayList<Long> getAllItems() {
        return receivedItems;
    }

    public static void updateEffect(MobEffectInstance effect, String key) {
        permanentEffects.put(key, effect);
    }

    public static void updateItem(ItemStack item, String key) {
        permanentItems.put(key, item);
    }

    @SubscribeEvent
    public static void onGameTick(TickEvent.ServerTickEvent event) {
        for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
            Set<String> foundItems = new HashSet<>();

            for (MobEffectInstance effect : permanentEffects.values()) {
                if (player.hasEffect(effect.getEffect())) {
                    if (player.getEffect(effect.getEffect()).getAmplifier() != effect.getAmplifier())
                        player.forceAddEffect(effect, player);
                }
                else {
                    player.forceAddEffect(effect, player);
                }
            }


            //check all inventory slots.
            for (Slot slot : player.containerMenu.slots) {
                if (!slot.hasItem())
                    continue;
                String key = slot.getItem().getOrCreateTag().getString("key");
                if (key.isBlank() || !permanentItems.containsKey(key))
                    continue;

                if (!ItemStack.isSameItemSameTags(slot.getItem(),permanentItems.get(key)) ||
                        (ItemStack.isSameItemSameTags(slot.getItem(),permanentItems.get(key)) &&
                                slot.getItem().getCount() != permanentItems.get(key).getCount())) {
                    slot.set(permanentItems.get(key).copy());
                }
                foundItems.add(key);
                player.containerMenu.broadcastFullState();
            }

            //check item on cursor.
            ItemStack carriedItem = player.containerMenu.getCarried();
            if (!carriedItem.isEmpty()) {
                String ckey = carriedItem.getOrCreateTag().getString("key");
                if (!ckey.isBlank()) {
                    if (!carriedItem.sameItem(permanentItems.get(ckey))) {
                        player.containerMenu.setCarried(permanentItems.get(ckey).copy());
                    }
                    foundItems.add(ckey);
                }
            }

            for (String key : permanentItems.keySet()) {
                if (!foundItems.contains(key)) {
                    player.getInventory().add(permanentItems.get(key).copy());
                }
            }
        }
    }
}
