package gg.archipelago.aprandomizer.apevents;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import dev.koifysh.archipelago.Print.APPrintColor;
import dev.koifysh.archipelago.events.ArchipelagoEventListener;
import dev.koifysh.archipelago.events.ReceiveItemEvent;
import dev.koifysh.archipelago.parts.NetworkItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;

public class ReceiveItem {

    @ArchipelagoEventListener
    public static void onReceiveItem(ReceiveItemEvent event) {
        // Dont fire if we have all ready recevied this location
        if(event.getIndex() <= APRandomizer.getWorldData().getItemIndex())
            return;

        APRandomizer.getWorldData().setItemIndex(event.getIndex());

        NetworkItem item = event.getItem();
        Component textItem = Component.literal(item.itemName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.gold.color.getRGB())));
        Component title = Component.literal("Received").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(APPrintColor.red.color.getRGB())));
        Utils.sendTitleToAll(title, textItem, 10, 60, 10);
        APRandomizer.getRecipeManager().grantRecipe(item.itemID);
        APRandomizer.getItemManager().giveItemToAll(item.itemID);

    }
}
