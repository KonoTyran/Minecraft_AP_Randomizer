package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.phys.Vec3;

public class GhastTrap implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();

            Ghast ghast = EntityType.GHAST.create(world);
            if (ghast == null)
                return;

            ghast.setTarget(player);
            ghast.getAttribute(Attributes.MAX_HEALTH).setBaseValue(15d);
            ghast.addEffect(new MobEffectInstance(MobEffects.WITHER, MobEffectInstance.INFINITE_DURATION));
            Vec3 offset = Utils.getRandomPosition(pos, 20);
            ghast.moveTo(offset);
            world.addFreshEntity(ghast);

        });
    }
}
