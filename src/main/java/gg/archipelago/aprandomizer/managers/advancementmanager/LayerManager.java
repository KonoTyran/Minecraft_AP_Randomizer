package gg.archipelago.aprandomizer.managers.advancementmanager;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber
public class LayerManager {

    private static final Set<Integer> checkLayers = new HashSet<>();

    private static final Set<Long> clearedLayers = new HashSet<>();

    public static final long START_INDEX = 42000;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        layerCheck:
        for (int y : checkLayers) {

            if(clearedLayers.contains((long)y))
                continue;
            Level overWorld = APRandomizer.server.getLevel(Level.OVERWORLD);
            assert overWorld != null;
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    BlockPos blockPos = new BlockPos(x,y,z);
                    BlockState block = overWorld.getBlockState(blockPos);
                    if(!block.isAir())
                        continue layerCheck;
                }
            }
            //layer clear? nice!!!
            clearedLayers.add((long)y);
            Utils.sendTitleToAll(Component.literal("Layer " + y +" clear!"),Component.empty(), 0,20,0);
            APRandomizer.getGoalManager().updateGoal(true);
            APRandomizer.getAP().checkLocation(y + 63 + START_INDEX);
        }
        checkLayers.clear();

    }

    public void addLayerCheck(int y) {
        if(y <= 128) {
            checkLayers.add(y);
        }
    }

    public void setCheckedLayers(Set<Long> locations) {
        clearedLayers.clear();
        for (Long location : locations) {
            clearedLayers.add(location - 63 - START_INDEX);
        }
    }

    public int getFinishedAmount() {
        return clearedLayers.size();
    }
}
