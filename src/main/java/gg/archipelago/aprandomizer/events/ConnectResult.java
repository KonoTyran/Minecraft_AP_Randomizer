package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.ClientStatus;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.ConnectionResultEvent;
import gg.archipelago.client.network.ConnectionResult;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConnectResult {

    private static final Logger LOGGER = LogManager.getLogger();
    APClient client;

    public ConnectResult(APClient apClient) {
        client = apClient;
    }

    @ArchipelagoEventListener
    public void onConnectResult(ConnectionResultEvent event) {
        if (event.getResult() == ConnectionResult.Success) {
            Utils.sendMessageToAll("Connected to Archipelago Server.");

            client.slotData = event.getSlotData(SlotData.class);

            APRandomizer.getLayerManager().setCheckedLayers(client.getLocationManager().getCheckedLocations());

            //give our item manager the list of received items to give to players as they log in.
            APRandomizer.getItemManager().setReceivedItems(client.getItemManager().getReceivedItemIDs());

            if(APRandomizer.getServer().getPlayerList().getPlayers().size() >= 1) {
                APRandomizer.getAP().setGameState(ClientStatus.CLIENT_READY);
            }

            //catch up all connected players to the list just received.
            APRandomizer.server.execute(() -> {
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
}
