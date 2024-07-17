package gg.archipelago.aprandomizer.events;

import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.PrintJSONEvent;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;

public class PrintJsonListener {

    @ArchipelagoEventListener
    public void onPrintJson(PrintJSONEvent event) {
        //don't print out messages if it's an item send and the recipient is us.
        if(event.type.equals("ItemSend") && event.player != APRandomizer.getAP().getSlot()) {
            Utils.sendFancyMessageToAll(event.apPrint);
        }
        else if(!event.type.equals("ItemSend")) {
            Utils.sendFancyMessageToAll(event.apPrint);
        }
    }
}
