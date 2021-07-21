package gg.archipelago.aprandomizer;

import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintColor;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.APClient.parts.NetworkItem;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APClient extends gg.archipelago.APClient.APClient {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private SlotData slotData;

    private final MinecraftServer server;

    APClient(MinecraftServer server) {
        super("Minecraft", APRandomizer.getApmcData().seed_name,APRandomizer.getApmcData().player_id);
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
    public void onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Connected to Archipelago Server.");
            slotData = event.getSlotData(SlotData.class);
            APMCData data = APRandomizer.getApmcData();
            if (!event.getSeedName().equals(data.seed_name)) {
                Utils.sendMessageToAll("Wrong .apmc file found. please stop the server, use the correct .apmc file, delete the world folder, then relaunch the server.");
                event.setCanceled(true);
            }
            if (slotData.getClient_version() != APRandomizer.getClientVersion()) {
                    event.setCanceled(true);
                    Utils.sendMessageToAll("AP server expects Minecraft Protocol version " + slotData.getClient_version() + " while current version is " + APRandomizer.getClientVersion());
                    return;
            }

            APRandomizer.getAdvancementManager().setCheckedAdvancements(getLocationManager().getCheckedLocations());

            //give our item manager the list of received items to give to players as they log in.
            APRandomizer.getItemManager().setReceivedItems(getItemManager().getReceivedItemIDs());

            //reset and catch up our global recipe list to be consistent with what we just got from the AP server
            APRandomizer.getRecipeManager().resetRecipes();
            APRandomizer.getRecipeManager().grantRecipeList(getItemManager().getReceivedItemIDs());

            //catch up all connected players to the list just received.
            server.execute(() -> {
                for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                    APRandomizer.getItemManager().catchUpPlayer(player);
                }
                APRandomizer.getBossBar().setMax(APRandomizer.getAdvancementManager().getRequiredAmount());
                APRandomizer.getBossBar().setName(new StringTextComponent(String.format("Advancements (%d / %d)", APRandomizer.getAdvancementManager().getFinishedAmount(), APRandomizer.getAdvancementManager().getRequiredAmount())));
                APRandomizer.getBossBar().setValue(APRandomizer.getAdvancementManager().getFinishedAmount());
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
    }

    @Override
    public void onReceiveItem(int item, String sentFromLocation, String senderName) {
        String itemName = getDataPackage().getItem(item);
        ITextComponent textItem = new StringTextComponent(itemName).withStyle(Style.EMPTY.withColor(Color.fromRgb(APPrintColor.gold.value)));
        ITextComponent chatMessage = new StringTextComponent(
                "Received ").withStyle(Style.EMPTY.withColor(Color.parseColor("red")))
                .append(itemName).withStyle(Style.EMPTY.withColor(Color.parseColor("gold")))
                .append(" from ").withStyle(Style.EMPTY.withColor(Color.parseColor("red")))
                .append(senderName).withStyle(Style.EMPTY.withColor(Color.parseColor("gold")))
                .append(" (").withStyle(Style.EMPTY.withColor(Color.parseColor("red")))
                .append(sentFromLocation).withStyle(Style.EMPTY.withColor(Color.parseColor("blue")))
                .append(")").withStyle(Style.EMPTY.withColor(Color.parseColor("red")));
        ITextComponent title = new StringTextComponent("Received").withStyle(Style.EMPTY.withColor(Color.fromRgb(APPrintColor.red.value)));
        Utils.sendTitleToAll(title, textItem, chatMessage, 10, 60, 10);
        APRandomizer.getRecipeManager().grantRecipe(item);
        APRandomizer.getItemManager().giveItemToAll(item);
    }
}
