package gg.archipelago.aprandomizer;

import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.events.AttemptedConnection;
import gg.archipelago.aprandomizer.events.ConnectResult;
import gg.archipelago.aprandomizer.events.ReceiveItem;
import gg.archipelago.aprandomizer.events.onBounced;
import gg.archipelago.client.ItemFlags;
import gg.archipelago.client.Print.APPrint;
import gg.archipelago.client.parts.NetworkItem;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APClient extends gg.archipelago.client.ArchipelagoClient {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SlotData slotData;

    private final MinecraftServer server;

    APClient(MinecraftServer server) {
        super();

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemFlags.SEND_ITEMS + ItemFlags.SEND_OWN_ITEMS + ItemFlags.SEND_STARTING_INVENTORY);
        this.server = server;

        //give our item manager the list of received items to give to players as they log in.
        APRandomizer.getItemManager().setReceivedItems(getItemManager().getReceivedItemIDs());

        this.getEventManager().registerListener(new ConnectResult(this));
        this.getEventManager().registerListener(new AttemptedConnection());
        this.getEventManager().registerListener(new ReceiveItem());
        this.getEventManager().registerListener(new onBounced());
    }

    public SlotData getSlotData() {
        return slotData;
    }


    @Override
    public void onPrint(String print) {
        if (!print.startsWith(getAlias() + ":")) {
            Utils.sendMessageToAll(print);
        }
    }

    @Override
    public void onPrintJson(APPrint apPrint, String type, int receiving, NetworkItem item) {
        if(apPrint.parts.length > 0) {
            if (!apPrint.parts[0].text.startsWith(getAlias() + ":")) {
                Utils.sendFancyMessageToAll(apPrint);
            }
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
}
