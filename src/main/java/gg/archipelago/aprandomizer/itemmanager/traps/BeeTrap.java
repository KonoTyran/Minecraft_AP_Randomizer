package gg.archipelago.aprandomizer.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.itemmanager.Trap;
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
                double radius = 5;
                double a = Math.random()*Math.PI*2;
                double b= Math.random()*Math.PI/2;
                double x = radius * Math.cos(a) * Math.sin(b) + pos.x;
                double z = radius * Math.sin(a) * Math.sin(b) + pos.z;
                double y = radius * Math.cos(b) + pos.y;
                Vector3d offset = new Vector3d(x,y,z);
                bee.moveTo(offset);
                bee.setPersistentAngerTarget(player.getUUID());
                bee.setRemainingPersistentAngerTime(1200);
                world.addFreshEntity(bee);
            }

        });
    }
}
