package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

public class PhantomTrap implements Trap {

    List<Phantom> phantoms = new LinkedList<>();

    int timer = 20 * 45;

    public PhantomTrap() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();
            for (int i = 0; i < 3; i++) {
                Phantom phantom = EntityType.PHANTOM.create(world);
                if (phantom == null)
                    continue;
                phantom.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE,MobEffectInstance.INFINITE_DURATION,0, false, false));
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                phantom.moveTo(offset);
                if (world.addFreshEntity(phantom))
                    phantoms.add(phantom);

            }
        });
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (--timer > 0)
            return;

        for (Phantom phantom : phantoms) {
            phantom.kill();
        }
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
