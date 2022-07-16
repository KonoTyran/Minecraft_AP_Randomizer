package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
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
        //TODO: this may be broken.
        String name = event.getEntityLiving().getEncodeId();

        if(APRandomizer.isConnected() && !APRandomizer.getAP().getSlotData().MC35)
            return;

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

    @SubscribeEvent
    static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity instanceof Pig) {
            if (entity.getPassengers().size() > 0) {
                if (entity.getPassengers().get(0) instanceof ServerPlayer) {
                    if (event.getSource().msgId.equals("fall")) {
                        ServerPlayer player = (ServerPlayer) entity.getPassengers().get(0);
                        Advancement advancement = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("aprandomizer:archipelago/ride_pig"));
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
        if (e instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) e;
            //Utils.sendMessageToAll("damage type: "+ event.getSource().getMsgId());
            if (event.getAmount() >= 18 && !event.getSource().isExplosion() && !event.getSource().msgId.equals("fireball")) {
                Advancement a = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("aprandomizer:archipelago/overkill"));
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
