package gg.archipelago.aprandomizer.common.events;

import dev.koifysh.archipelago.network.client.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

@Mod.EventBusSubscriber
public class onLivingHurt {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onLivingDeathEvent(LivingDeathEvent event) {
        if(APRandomizer.isConnected() && !APRandomizer.getAP().getSlotData().MC35)
            return;

        //TODO: this may be broken.
        String name = event.getEntity().getEncodeId();

        Entity damageSource = event.getSource().getEntity();
        if(damageSource != null && damageSource.getType() == EntityType.PLAYER) {
            BouncePacket packet = new BouncePacket();
            packet.tags = new String[]{"MC35"};
            packet.setData(new HashMap<String, Object>() {{
                put("enemy", name);
                put("source", APRandomizer.getAP().getSlot());
                CompoundTag nbt = event.getEntity().saveWithoutId(new CompoundTag());
                nbt.remove("UUID");
                nbt.remove("Motion");
                nbt.remove("Health");
                //LOGGER.info("nbt: {}",nbt.toString());
                put("nbt", nbt.toString());
            }});
            APRandomizer.sendBounce(packet);
        }
    }
}
