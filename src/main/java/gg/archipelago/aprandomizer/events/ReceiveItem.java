package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.ReceiveItemEvent;

public class ReceiveItem {

    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        APRandomizer.getItemManager().giveItemToAll(event.getItemID(), event.getIndex());
    }
}
