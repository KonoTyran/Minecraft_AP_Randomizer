package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.entity.Entity;
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
    static void onLivingHurtEvent(LivingHurtEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (entity.getEntity().getEntity() instanceof PigEntity) {
            if(entity.getPassengers().size() >0 ) {
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
        Entity e =event.getSource().getEntity();
        if (e instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity)e;
            if (event.getAmount() >= 18) {
                Advancement a = event.getEntityLiving().getServer().getAdvancements().getAdvancement(new ResourceLocation("minecraft:story/overkill"));
                AdvancementProgress ap = player.getAdvancements().getOrStartProgress(a);
                if (!ap.isDone()) {
                    for(String s : ap.getRemainingCriteria()) {
                        player.getAdvancements().award(a, s);
                    }
                }
            }
        }
    }
}
