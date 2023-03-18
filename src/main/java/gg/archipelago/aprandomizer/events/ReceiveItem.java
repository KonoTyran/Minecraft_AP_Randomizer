package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.client.Print.APPrintColor;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.ReceiveItemEvent;
import gg.archipelago.client.parts.NetworkItem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ReceiveItem {

    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        NetworkItem item = event.getItem();
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

        APRandomizer.getItemManager().giveItemToAll(item.itemID);
    }
}
