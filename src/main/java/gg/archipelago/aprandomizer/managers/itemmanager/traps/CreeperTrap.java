package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.phys.Vec3;

public class CreeperTrap implements Trap {

    final private int numberOfCreepers;

    public CreeperTrap(int numberOfCreepers) {
        this.numberOfCreepers = numberOfCreepers;
    }

    public CreeperTrap() {
        this(3);
    }

    @Override
    public void trigger(ServerPlayer player) {
        APRandomizer.getServer().execute(() -> {
            ServerLevel world = player.getLevel();
            Vec3 pos = player.position();
            for (int i = 0; i < numberOfCreepers; i++) {
                Creeper creeper = EntityType.CREEPER.create(world);
                if(creeper == null)
                    continue;
                creeper.setTarget(player);
                Vec3 offset = Utils.getRandomPosition(pos, 5);
                creeper.moveTo(offset);
                world.addFreshEntity(creeper);
            }
        });
    }
}
