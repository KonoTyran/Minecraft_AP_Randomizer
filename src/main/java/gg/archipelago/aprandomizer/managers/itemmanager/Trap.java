package gg.archipelago.aprandomizer.managers.itemmanager;

import net.minecraft.server.level.ServerPlayer;

public interface Trap {

    void trigger(ServerPlayer player);
}