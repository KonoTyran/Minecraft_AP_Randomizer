package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onAdvancement {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onAdvancementEvent(AdvancementEvent event) {
        //dont do any checking if the apmcdata file is not valid.
        if (APRandomizer.getApmcData().state != APMCData.State.VALID)
            return;

        ServerPlayer player = (ServerPlayer) event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String id = advancement.getId().toString();

        AdvancementManager am = APRandomizer.getAdvancementManager();
        //don't do anything if this advancement has already been had, or is not on our list of tracked advancements.
        if (!am.hasAdvancement(id) && am.getAdvancementID(id) != 0) {
            LOGGER.debug("{} has gotten the advancement {}", player.getDisplayName().getString(), id);
            am.addAdvancement(am.getAdvancementID(id));
            APRandomizer.getServer().getPlayerList().broadcastMessage(
                    new TranslatableComponent(
                            "chat.type.advancement."
                                    + advancement.getDisplay().getFrame().getName(),
                            player.getDisplayName(),
                            advancement.getChatComponent()
                    ),
                    ChatType.SYSTEM,
                    Util.NIL_UUID
            );

            am.syncAdvancement(advancement);
        }
    }
}
