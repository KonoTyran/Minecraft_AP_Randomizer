package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import net.minecraft.server.level.ServerPlayer;

public class AboutFaceTrap implements Trap {
    @Override
    public void trigger(ServerPlayer player) {
        player.teleportTo(player.getLevel(),player.getX(),player.getY(),player.getZ(),player.getYHeadRot() + 180f, player.getXRot());
    }
}
