package gg.archipelago.aprandomizer.managers.itemmanager.traps;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.LinkedList;
import java.util.List;

public class WaterTrap implements Trap {

    List<BlockPos> waterBlocks = new LinkedList<>();
    int timer = 20 * 15;

    public WaterTrap() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void trigger(ServerPlayer player) {
        ServerLevel world = APRandomizer.getServer().overworld();
        Vec3 pos = player.position();
        int radius = 2;
        for (int x = (int) pos.x - radius; x <= (int) pos.x + radius; x++) {
            for (int z = (int) pos.z - radius; z <= (int) pos.z + radius; z++) {
                waterBlocks.add(new BlockPos(x, (int) pos.y + 3, z));
            }
        }

        APRandomizer.getServer().execute(() -> {
            for (BlockPos waterBlock : waterBlocks) {
                    if (world.isEmptyBlock(waterBlock)) {
                        world.setBlock(waterBlock, Blocks.WATER.defaultBlockState(), 3);
                    }
                }
            });
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (--timer > 0)
            return;

        for (BlockPos pos : waterBlocks) {
            ServerLevel world = APRandomizer.getServer().overworld();
            if (world.getBlockState(pos).getFluidState().isSourceOfType(Fluids.WATER)) {
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }

        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
