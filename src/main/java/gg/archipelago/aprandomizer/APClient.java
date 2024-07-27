package gg.archipelago.aprandomizer;

import gg.archipelago.aprandomizer.apevents.*;
import dev.koifysh.archipelago.ItemFlags;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class APClient extends dev.koifysh.archipelago.Client {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    public SlotData slotData;

    private final MinecraftServer server;

    APClient(MinecraftServer server) {
        super();

        this.setGame("Minecraft");
        this.setItemsHandlingFlags(ItemFlags.SEND_ITEMS + ItemFlags.SEND_OWN_ITEMS + ItemFlags.SEND_STARTING_INVENTORY);
        this.server = server;
        APRandomizer.getAdvancementManager().setCheckedAdvancements(getLocationManager().getCheckedLocations());

        //give our item manager the list of received items to give to players as they log in.
        APRandomizer.getItemManager().setReceivedItems(getItemManager().getReceivedItemIDs());

        //reset and catch up our global recipe list to be consistent with what we loaded from our save file.
        APRandomizer.getRecipeManager().resetRecipes();
        APRandomizer.getRecipeManager().grantRecipeList(getItemManager().getReceivedItemIDs());

        this.getEventManager().registerListener(new onDeathLink());
        this.getEventManager().registerListener(new onMC35());
        this.getEventManager().registerListener(new ConnectResult(this));
        this.getEventManager().registerListener(new AttemptedConnection());
        this.getEventManager().registerListener(new ReceiveItem());
        this.getEventManager().registerListener(new LocationChecked());
        this.getEventManager().registerListener(new PrintJsonListener());
    }

    public SlotData getSlotData() {
        return slotData;
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
