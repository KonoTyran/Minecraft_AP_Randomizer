package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
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
            LOGGER.debug("{} has gotten the advancement {}",player.getDisplayName().getString(),id);
            am.addAdvancement(am.getAdvancementID(id));

            String remaining = String.format(" (%d)",am.getFinishedAmount());
            if(am.getRequiredAmount() > 0)
                remaining = String.format(" (%d / %d)",am.getFinishedAmount(),am.getRequiredAmount());

            APRandomizer.getServer().getPlayerList().broadcastMessage(
                    new TranslationTextComponent(
                            "chat.type.advancement."
                                    + advancement.getDisplay().getFrame().getName(),
                            player.getDisplayName(),
                            advancement.getChatComponent()
                    ).append(
                            new StringTextComponent(
                                    remaining
                            )
                    ),
                    ChatType.SYSTEM,
                    Util.NIL_UUID
            );

            am.syncAdvancement(advancement);
            if(am.getRequiredAmount() != 0) {
                if(am.getFinishedAmount() >= am.getRequiredAmount()) {
                    ServerWorld end = event.getPlayer().getServer().getLevel(World.END);
                    assert end != null;
                    assert end.dragonFight != null;
                    WorldData worldData = end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);

                    if(worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
                        Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
                        Utils.sendMessageToAll("The Dragon has awoken.");
                        Utils.sendTitle(new StringTextComponent("Ender Dragon").withStyle(Style.EMPTY.withColor(Color.fromRgb(java.awt.Color.ORANGE.getRGB()))),new StringTextComponent("has been awoken"),40,120,40);
                        worldData.setDragonState(WorldData.DRAGON_SPAWNED);
                        Utils.SpawnDragon(end);
                    }
                }
            }
        }
    }
}
