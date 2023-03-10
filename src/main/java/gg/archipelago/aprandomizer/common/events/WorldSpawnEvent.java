package gg.archipelago.aprandomizer.common.events;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class WorldSpawnEvent {

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        Player player = e.getEntity();
        if (player.level.isClientSide) {
            return;
        }

        Level level = player.level;
        BlockPos spawn = level.getSharedSpawnPos();
        player.teleportTo(spawn.getX(),spawn.getY(),spawn.getZ());
    }
}
