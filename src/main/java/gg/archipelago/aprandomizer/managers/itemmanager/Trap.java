package gg.archipelago.aprandomizer.managers.itemmanager;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface Trap {

    void trigger(ServerPlayerEntity player);
}