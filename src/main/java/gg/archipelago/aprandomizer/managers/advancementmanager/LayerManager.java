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

    private static final Set<Integer> clearedLayers = new HashSet<>();

    public static final long START_INDEX = 42000;

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        layerCheck:
        for (int y : checkLayers) {

            if(clearedLayers.contains(y))
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
            clearedLayers.add(y);
            Utils.sendTitleToAll(Component.literal("Layer " + y +" clear!"),Component.empty(), 10,20,10);
            APRandomizer.getGoalManager().updateInfoBar();
            APRandomizer.getAP().checkLocation(y + 64 + START_INDEX);
        }
        checkLayers.clear();

    }

    public void addLayerCheck(int y) {
        checkLayers.add(y);
    }

    public void setCheckedLayers(Set<Integer> locations) {
        clearedLayers.clear();
        clearedLayers.addAll(locations);
    }

    public int getFinishedAmount() {
        return clearedLayers.size();
    }
}
