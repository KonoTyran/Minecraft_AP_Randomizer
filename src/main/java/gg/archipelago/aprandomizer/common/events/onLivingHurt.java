package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
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
        ResourceLocation location = event.getEntity().getType().getRegistryName();
        if(location == null)
            return;

        if(APRandomizer.isConnected() && !APRandomizer.getAP().getSlotData().isMC35())
            return;

        Entity damageSource = event.getSource().getEntity();
        if(damageSource != null && damageSource.getType() == EntityType.PLAYER) {
            BouncePacket packet = new BouncePacket();
            packet.tags = new String[]{"MC35"};
            packet.setData(new HashMap<String, Object>() {{
                put("enemy", location.toString());
                put("source", APRandomizer.getAP().getSlot());
                CompoundNBT nbt = event.getEntity().saveWithoutId(new CompoundNBT());
                nbt.remove("UUID");
                nbt.remove("Motion");
                nbt.remove("Health");
                //LOGGER.info("nbt: {}",nbt.toString());
                put("nbt", nbt.toString());
            }});
            APRandomizer.sendBounce(packet);
        }
    }

    @SubscribeEvent
    static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getEntity().getEntity() instanceof PigEntity) {
            if (entity.getPassengers().size() > 0) {
                if (entity.getPassengers().get(0) instanceof ServerPlayerEntity) {
                    if (event.getSource().msgId.equals("fall")) {
                        ServerPlayerEntity player = (ServerPlayerEntity) entity.getPassengers().get(0);
                        Advancement advancement = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:husbandry/ride_pig"));
                        AdvancementProgress ap = player.getAdvancements().getOrStartProgress(advancement);
                        if (!ap.isDone()) {
                            for (String s : ap.getRemainingCriteria()) {
                                player.getAdvancements().award(advancement, s);
                            }
                        }
                    }
                }
            }
        }

        Entity e = event.getSource().getEntity();
        if (e instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) e;
            //Utils.sendMessageToAll("damage type: "+ event.getSource().getMsgId());
            if (event.getAmount() >= 18 && !event.getSource().isExplosion() && !event.getSource().msgId.equals("fireball")) {
                Advancement a = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/overkill"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(a);
                if (!ap.isDone()) {
                    for (String s : ap.getRemainingCriteria()) {
                        player.getAdvancements().award(a, s);
                    }
                }
            }
        }
    }
}
