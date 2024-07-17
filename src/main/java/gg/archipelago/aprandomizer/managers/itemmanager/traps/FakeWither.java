package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
public class FakeWither implements Trap {

    private static final CustomBossEvent witherBar;

    static {
        witherBar = APRandomizer.getServer().getCustomBossEvents().create(ResourceLocation.fromNamespaceAndPath(APRandomizer.MODID,"fake-wither"),Component.translatable(EntityType.WITHER.getDescriptionId()));
        witherBar.setColor(BossEvent.BossBarColor.PURPLE);
        witherBar.setDarkenScreen(true);
        witherBar.setMax(300);
        witherBar.setValue(0);
        witherBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);
        witherBar.setVisible(false);
    }

    public FakeWither() {
        MinecraftForge.EVENT_BUS.register(this);
    }
    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            witherBar.addPlayer(player);
            witherBar.setVisible(true);
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS,20*6, 0));
            player.playNotifySound(SoundEvents.WITHER_SPAWN, SoundSource.MASTER, 1, 1);
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.side == LogicalSide.CLIENT)
            return;
        if(!witherBar.isVisible())
            return;
        int value = witherBar.getValue();
        if(value >= witherBar.getMax()) {
            witherBar.setValue(0);
            witherBar.setVisible(false);
            MinecraftForge.EVENT_BUS.unregister(this);
            return;
        }
        witherBar.setValue(++value);
    }
}