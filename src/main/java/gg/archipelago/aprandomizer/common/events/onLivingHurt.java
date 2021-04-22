package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber
public class onLivingHurt {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onLivingHurt(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getEntity().getEntity() instanceof PigEntity) {
            if (entity.getPassengers().get(0) instanceof PlayerEntity) {
                if(event.getSource().msgId.equals("fall")) {
                    ServerPlayerEntity e = (ServerPlayerEntity)entity.getPassengers().get(0);
                    Advancement a = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:husbandry/ride_pig"));
                    AdvancementProgress ap = e.getAdvancements().getOrStartProgress(a);
                    if (!ap.isDone()) {
                        for(String s : ap.getRemainingCriteria()) {
                            e.getAdvancements().award(a, s);
                        }
                    }

                }
            }
        }
    }
}
