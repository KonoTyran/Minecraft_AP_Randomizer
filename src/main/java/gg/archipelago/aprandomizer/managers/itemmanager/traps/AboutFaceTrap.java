package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class AboutFaceTrap implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        player.teleportTo((ServerLevel) player.level(),player.getX(),player.getY(),player.getZ(),player.getYHeadRot() + 180f, player.getXRot());
    }
}