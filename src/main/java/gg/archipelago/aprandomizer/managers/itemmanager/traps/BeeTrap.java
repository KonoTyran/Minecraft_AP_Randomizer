package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.itemmanager.Trap;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;

public class BeeTrap implements Trap {

    final private int numberOfBees;

    public BeeTrap(int numberOfBees) {
        this.numberOfBees = numberOfBees;
    }
    @Override
    public void trigger(ServerPlayerEntity player) {
        APRandomizer.getServer().execute(() -> {
            ServerWorld world = player.getLevel();
            Vector3d pos = player.position();
            for (int i = 0; i < numberOfBees; i++) {
                BeeEntity bee = EntityType.BEE.create(world);
                Vector3d offset = Utils.getRandomPosition(pos, 5);
                bee.moveTo(offset);
                bee.setPersistentAngerTarget(player.getUUID());
                bee.setRemainingPersistentAngerTime(1200);
                world.addFreshEntity(bee);
            }

        });
    }
}
