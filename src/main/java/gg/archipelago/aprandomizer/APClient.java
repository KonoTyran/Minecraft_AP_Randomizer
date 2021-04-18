package gg.archipelago.aprandomizer;

import gg.archipelago.APClient.Print.APPrint;
import gg.archipelago.APClient.Print.APPrintColor;
import gg.archipelago.APClient.events.ConnectionResultEvent;
import gg.archipelago.APClient.network.ConnectionResult;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.util.text.Color;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APClient extends gg.archipelago.APClient.APClient {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private final MinecraftServer server;
    APClient(MinecraftServer server) {
        super("Minecraft");
        this.server = server;
    }

    @Override
    public boolean onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Connected to Archipelago Server.");

        }
        else if (event.getResult() == ConnectionResult.InvalidPassword) {
            Utils.sendMessageToAll("Invalid Password.");
        }
        else if (event.getResult() == ConnectionResult.IncompatibleVersion) {
            Utils.sendMessageToAll("Minecraft Randomizer Client Outdated.");
        }
        else if (event.getResult() == ConnectionResult.InvalidSlot) {
            Utils.sendMessageToAll("Invalid Slot Name. (this is case sensitive)");
        }
        else if (event.getResult() == ConnectionResult.SlotAlreadyTaken) {
            Utils.sendMessageToAll("Room Slot has all ready been taken.");
        }

        return false;
    }

    @Override
    public void onJoinRoom() {

    }

    @Override
    public void onPrint(String print) {
        if (!print.startsWith(getAlias()+":")) {
            Utils.sendMessageToAll(print);
        }
    }

    @Override
    public void onPrintJson(APPrint apPrint) {
        LOGGER.trace("onPrintJson fired");
        Utils.sendFancyMessageToAll(apPrint);
    }

    @Override
    public void onError(Exception ex) {
        String error = String.format("Connection error: %s", ex.getLocalizedMessage());
        Utils.sendMessageToAll(error);
    }

    @Override
    public void onClose(String reason, int attemptingReconnect) {
        if (attemptingReconnect > 0) {
            Utils.sendMessageToAll(String.format("%s \n... reconnecting in %ds",reason, attemptingReconnect));
        } else {
            Utils.sendMessageToAll(reason);
        }
    }

    @Override
    public void onReceiveItem(int item, String sentFromLocation, String senderName) {
        String itemName = getDataPackage().getItem(item);
        ITextComponent textItem = new StringTextComponent(itemName).withStyle(Style.EMPTY.withColor(Color.fromRgb(APPrintColor.gold.value)));
        Utils.sendTitle(server, new StringTextComponent("Received").withStyle(Style.EMPTY.withColor(Color.fromRgb(APPrintColor.red.value))), textItem, 10, 60, 10);
        APRandomizer.getRecipeManager().grantRecipe(item);
        APRandomizer.getItemManager().giveItem(item);
    }

    @Override
    public void receiveItems(int item) {

    }
}
