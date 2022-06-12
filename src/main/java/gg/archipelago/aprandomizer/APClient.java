package gg.archipelago.aprandomizer;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import gg.archipelago.APClient.ItemFlags;
import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintColor;
import gg.archipelago.APClient.events.ConnectionAttemptEvent;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.network.BouncedPacket;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.APClient.parts.NetworkItem;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.DeathLinkDamage;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class APClient extends gg.archipelago.APClient.APClient {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private SlotData slotData;

    private final MinecraftServer server;

    APClient(MinecraftServer server) {
        super(APRandomizer.getApmcData().seed_name,APRandomizer.getApmcData().player_id);

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemFlags.SEND_ITEMS + ItemFlags.SEND_OWN_ITEMS + ItemFlags.SEND_STARTING_INVENTORY);
        this.server = server;
        APRandomizer.getAdvancementManager().setCheckedAdvancements(getLocationManager().getCheckedLocations());

        //give our item manager the list of received items to give to players as they log in.
        APRandomizer.getItemManager().setReceivedItems(getItemManager().getReceivedItemIDs());

        //reset and catch up our global recipe list to be consistent with what we loaded from our save file.
        APRandomizer.getRecipeManager().resetRecipes();
        APRandomizer.getRecipeManager().grantRecipeList(getItemManager().getReceivedItemIDs());
    }

    public SlotData getSlotData() {
        return slotData;
    }

    @Override
    public void onAttemptConnection(ConnectionAttemptEvent event) {
        try {
            SlotData temp = event.getSlotData(SlotData.class);
            APMCData data = APRandomizer.getApmcData();
            if (!event.getSeedName().equals(data.seed_name)) {
                Utils.sendMessageToAll("Wrong .apmc file found. please stop the server, use the correct .apmc file, delete the world folder, then relaunch the server.");
                event.setCanceled(true);
            }
            if (!APRandomizer.getValidVersions().contains(temp.getClient_version())) {
                event.setCanceled(true);
                Utils.sendMessageToAll("Game was generated with an for an incompatible version of the Minecraft Randomizer.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Connected to Archipelago Server.");
            try {
                slotData = event.getSlotData(SlotData.class);
                slotData.parseStartingItems();
            } catch (Exception e) {
                Utils.sendMessageToAll("Invalid staring item section, check logs for more details.");
                LOGGER.warn("invalid staring items json string: " + slotData.startingItems);
            }

            HashSet<String> tags = new HashSet<>();
            if(slotData.MC35) {
                tags.add("MC35");
            }
            if(slotData.deathlink) {
                Utils.sendMessageToAll("Welcome to Death Link.");
                tags.add("DeathLink");
            }
            if(!tags.isEmpty()) {
                addTags(tags);
            }

            APRandomizer.getAdvancementManager().setCheckedAdvancements(getLocationManager().getCheckedLocations());

            //give our item manager the list of received items to give to players as they log in.
            APRandomizer.getItemManager().setReceivedItems(getItemManager().getReceivedItemIDs());

            //reset and catch up our global recipe list to be consistent with what we just got from the AP server
            APRandomizer.getRecipeManager().resetRecipes();
            APRandomizer.getRecipeManager().grantRecipeList(getItemManager().getReceivedItemIDs());

            //catch up all connected players to the list just received.
            server.execute(() -> {
                for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                    APRandomizer.getItemManager().catchUpPlayer(player);
                }
                APRandomizer.getGoalManager().updateInfoBar();
            });

        } else if (event.getResult() == ConnectionResult.InvalidPassword) {
            Utils.sendMessageToAll("Invalid Password.");
        } else if (event.getResult() == ConnectionResult.IncompatibleVersion) {
            Utils.sendMessageToAll("Server Sent Incompatible Version Error.");
        } else if (event.getResult() == ConnectionResult.InvalidSlot) {
            Utils.sendMessageToAll("Invalid Slot Name. (this is case sensitive)");
        } else if (event.getResult() == ConnectionResult.SlotAlreadyTaken) {
            Utils.sendMessageToAll("Room Slot has all ready been taken.");
        }
    }

    @Override
    public void onJoinRoom() {

    }

    @Override
    public void onBounced(BouncedPacket packet) {
        if(packet.tags.contains("DeathLink") && APRandomizer.getAP().getSlotData().deathlink) {
            if(packet.getDouble("time") == APRandomizer.getLastDeathTimestamp())
                return;
            DeathLinkDamage deathLink = DeathLinkDamage.DEATH_LINK;
            GameRules.BooleanValue showDeathMessages = APRandomizer.getServer().getGameRules().getRule(GameRules.RULE_SHOWDEATHMESSAGES);
            boolean showDeaths = showDeathMessages.get();
            if(showDeaths) {
                String cause = packet.getString("cause");
                if(cause != null && !cause.isBlank())
                    Utils.sendMessageToAll(packet.getString("cause"));
                else
                    Utils.sendMessageToAll("This Death brought to you by "+ packet.getString("source"));
            }
            showDeathMessages.set(false,APRandomizer.getServer());
            for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                player.hurt(deathLink , Float.MAX_VALUE);
            }
            showDeathMessages.set(showDeaths,APRandomizer.getServer());
        }
        if(packet.tags.contains("MC35") && APRandomizer.getAP().getSlotData().MC35) {
            int sourceSlot = packet.getInt("source");
            if(sourceSlot != APRandomizer.getAP().getSlot()) {
                int randPlayer = ThreadLocalRandom.current().nextInt(server.getPlayerCount());
                ServerPlayer player = server.getPlayerList().getPlayers().get(randPlayer);
                CompoundTag eNBT = new CompoundTag();
                try {
                    if(packet.containsKey("nbt"))
                        eNBT = TagParser.parseTag(packet.getString("nbt"));
                } catch (CommandSyntaxException ignored) {}
                eNBT.putString("id", packet.getString("enemy"));
                Entity entity = EntityType.loadEntityRecursive(eNBT, player.level, (spawnEntity) -> {
                    Vec3 pos = player.position();
                    Vec3 offset = Utils.getRandomPosition(pos,10);
                    spawnEntity.moveTo(offset.x, offset.y, offset.z, spawnEntity.yRotO, spawnEntity.xRotO);
                    return spawnEntity;
                });
                if(entity != null) {
                    if(entity instanceof LivingEntity) {
                        ((LivingEntity)entity).heal(((LivingEntity) entity).getMaxHealth());
                        ((LivingEntity)entity).setLastHurtByPlayer(player);
                    }
                    player.getLevel().addFreshEntity(entity);
                }
            }
            return;
        }
    }

    @Override
    public void onPrint(String print) {
        if (!print.startsWith(getAlias() + ":")) {
            Utils.sendMessageToAll(print);
        }
    }

    @Override
    public void onPrintJson(APPrint apPrint, String type, int receiving, NetworkItem item) {

        //don't print out messages if its an item send and the recipient is us.
        if(type.equals("ItemSend") && receiving != getSlot()) {
            Utils.sendFancyMessageToAll(apPrint);
        }
        else if(!type.equals("ItemSend")) {
            Utils.sendFancyMessageToAll(apPrint);
        }
    }

    @Override
    public void onError(Exception ex) {
        String error = String.format("Connection error: %s", ex.getLocalizedMessage());
        Utils.sendMessageToAll(error);
    }

    @Override
    public void onClose(String reason, int attemptingReconnect) {
        if (attemptingReconnect > 0) {
            Utils.sendMessageToAll(String.format("%s \n... reconnecting in %ds", reason, attemptingReconnect));
        } else {
            Utils.sendMessageToAll(reason);
        }
        APRandomizer.getGoalManager().updateInfoBar();
    }

    @Override
    public void onReceiveItem(NetworkItem item) {
        Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.color.getRGB())));
        Component chatMessage = Component.literal(
                "Received ").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red")))
                .append(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.parseColor("gold")))
                .append(" from ").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red")))
                .append(item.playerName).withStyle(Style.EMPTY.withColor(TextColor.parseColor("gold")))
                .append(" (").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red")))
                .append(item.locationName).withStyle(Style.EMPTY.withColor(TextColor.parseColor("blue")))
                .append(")").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red")));
        Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.red.color.getRGB())));
        Utils.sendTitleToAll(title, textItem, chatMessage, 10, 60, 10);
        APRandomizer.getRecipeManager().grantRecipe(item.itemID);
        APRandomizer.getItemManager().giveItemToAll(item.itemID);
    }

    @Override
    public void onLocationInfo(ArrayList<NetworkItem> arrayList) {}

    @Override
    public void onLocationChecked(int locationID) {
        APRandomizer.getAdvancementManager().addAdvancement(locationID);
    }

    public void addTag(String tag) {
        HashSet<String> tags = new HashSet<>(Arrays.asList(getTags()));
        tags.add(tag);
        setTags(tags.toArray(String[]::new));
    }

    public boolean removeTag(String tag) {
        HashSet<String> tags = new HashSet<>(Arrays.asList(getTags()));
        boolean flag = tags.remove(tag);
        if(flag)
            setTags(tags.toArray(String[]::new));
        return flag;
    }

    public void addTags(HashSet<String> toAdd) {
        HashSet<String> tags = new HashSet<>(Arrays.asList(getTags()));
        tags.addAll(toAdd);

        setTags(tags.toArray(String[]::new));
    }
}
