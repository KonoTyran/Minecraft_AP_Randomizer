package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.PlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onPlayerClone {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (APRandomizer.isJailPlayers()) {
            BlockPos jail = APRandomizer.getJailPosition();
            event.getEntity().teleportTo(jail.getX(), jail.getY(), jail.getZ());
        }

        MobEffectInstance saturation = new MobEffectInstance(MobEffects.SATURATION,MobEffectInstance.INFINITE_DURATION,0,false,false,false);
        event.getEntity().addEffect(saturation);

        MobEffectInstance nightVision = new MobEffectInstance(MobEffects.NIGHT_VISION,MobEffectInstance.INFINITE_DURATION,0,false,false,false);
        event.getEntity().addEffect(nightVision);
    }

    @SubscribeEvent
    public static void onPlayerCloneEvent(PlayerEvent.Clone event) {
        if(!event.isWasDeath())
            return;

        event.getOriginal().reviveCaps();

        event.getEntity().getCapability(APCapabilities.PLAYER_INDEX).ifPresent(playerData -> {
            LazyOptional<PlayerData> lazyOptional = event.getOriginal().getCapability(APCapabilities.PLAYER_INDEX);
            if (lazyOptional.isPresent()) {
                playerData.setIndex(lazyOptional.orElseThrow(AssertionError::new).getIndex());
            }
            else {
                APRandomizer.LOGGER.error("unable to copy player index for player " + event.getEntity().getDisplayName().getString());
            }
        });

        event.getOriginal().invalidateCaps();
    }
}
