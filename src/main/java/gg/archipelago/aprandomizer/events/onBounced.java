package gg.archipelago.aprandomizer.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.client.ClientStatus;
import gg.archipelago.client.events.ArchipelagoEventListener;
import gg.archipelago.client.events.BouncedEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.border.WorldBorder;

public class onBounced {

    @ArchipelagoEventListener
    public static void received(BouncedEvent event) {
        if (event.containsKey("start") && event.getBoolean("start") && APRandomizer.isJailPlayers()) {
            Utils.sendTitleToAll(Component.literal("Dig"), Component.literal("The Chunk"), 20 * 2, 20 * 5, 20 * 3);
            if (APRandomizer.isConnected()) {
                APRandomizer.getAP().setGameState(ClientStatus.CLIENT_PLAYING);
            }
            APRandomizer.setJailPlayers(false);

            WorldBorder border = APRandomizer.getServer().overworld().getWorldBorder();
            border.setCenter(7.0, 7.0);
            border.setSize(24.0);
            border.setWarningBlocks(0);
            border.setWarningTime(0);
            border.setDamageSafeZone(0);
            border.setDamagePerBlock(Double.MAX_VALUE);

            ItemStack scaffolding = new ItemStack(Items.SCAFFOLDING, 64);
            scaffolding.getOrCreateTag().putString("key", "scaffolding");
            ItemManager.updateItem(scaffolding, "scaffolding");

            APRandomizer.getServer().execute(() -> {
                for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                    player.seenCredits = true;
                    player.changeDimension(APRandomizer.getServer().overworld());
                }
            });
        }
    }
}
