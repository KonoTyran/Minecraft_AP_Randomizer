package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.SlotData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.ConnectionResultEvent;
import gg.archipelago.client.network.ConnectionResult;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;

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
            try {
                client.slotData = event.getSlotData(SlotData.class);
                client.slotData.parseStartingItems();
            } catch (Exception e) {
                Utils.sendMessageToAll("Invalid staring item section, check logs for more details.");
                LOGGER.warn("invalid staring items json string: " + client.slotData.startingItems);
            }

            HashSet<String> tags = new HashSet<>();
            if(client.slotData.MC35) {
                client.addTag("MC35");
            }
            if(client.slotData.deathlink) {
                Utils.sendMessageToAll("Welcome to Death Link.");
                client.addTag("DeathLink");
            }

            APRandomizer.getAdvancementManager().setCheckedAdvancements(client.getLocationManager().getCheckedLocations());

            //give our item manager the list of received items to give to players as they log in.
            APRandomizer.getItemManager().setReceivedItems(client.getItemManager().getReceivedItemIDs());

            //reset and catch up our global recipe list to be consistent with what we just got from the AP server
            APRandomizer.getRecipeManager().resetRecipes();
            APRandomizer.getRecipeManager().grantRecipeList(client.getItemManager().getReceivedItemIDs());

            //catch up all connected players to the list just received.
            APRandomizer.server.execute(() -> {
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
}
