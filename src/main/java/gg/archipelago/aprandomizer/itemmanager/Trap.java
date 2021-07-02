package gg.archipelago.aprandomizer.itemmanager;

import net.minecraft.entity.player.ServerPlayerEntity;

public interface Trap {

    void trigger(ServerPlayerEntity player);
}