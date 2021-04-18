package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
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
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        Advancement advancement = event.getAdvancement();
        String id = advancement.getId().toString();

        AdvancementManager am = APRandomizer.getAdvancementManager();
        if(!am.hasAdvancement(id)) {
            LOGGER.info("{} has gotten the advancement {}",player.getDisplayName().getString(),id);
            am.addAdvancement(am.getAdvancementID(id));
            APRandomizer.getServer().getPlayerList().broadcastMessage(new TranslationTextComponent("chat.type.advancement." + advancement.getDisplay().getFrame().getName(), player.getDisplayName(), advancement.getChatComponent()).append(new StringTextComponent(" ("+am.getFinishedAmount()+")")), ChatType.SYSTEM, Util.NIL_UUID);

            am.syncAdvancement(advancement);
        }
    }
}
