package gg.archipelago.aprandomizer.managers;

import gg.archipelago.aprandomizer.managers.advancementmanager.LayerManager;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.client.ClientStatus;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.world.BossEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GoalManager {

    private final LayerManager layerManager;

    private CustomBossEvent layerDugBar;
    private CustomBossEvent connectionInfoBar;

    private final APMCData apmc;

    public GoalManager () {
        apmc = APRandomizer.getApmcData();
        layerManager = APRandomizer.getLayerManager();
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        CustomBossEvents bossInfoManager = APRandomizer.getServer().getCustomBossEvents();
        layerDugBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"layer-dug-bar"), Component.literal(""));
        layerDugBar.setMax(192);
        layerDugBar.setColor(BossEvent.BossBarColor.BLUE);
        layerDugBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);

        connectionInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"connection-info-bar"), Component.literal("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red"))));
        connectionInfoBar.setMax(1);
        connectionInfoBar.setValue(1);
        connectionInfoBar.setColor(BossEvent.BossBarColor.RED);
        connectionInfoBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);

        updateInfoBar();
        layerDugBar.setVisible(true);
        connectionInfoBar.setVisible(true);
    }

    public void updateGoal(boolean canFinish) {
        updateInfoBar();
        if(canFinish)
            checkGoalCompletion();
    }


    public void updateInfoBar() {
        if(layerDugBar == null)
            return;
        APRandomizer.getServer().execute(() -> {
            layerDugBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
            connectionInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
        });

        layerDugBar.setValue(layerManager.getFinishedAmount());

        connectionInfoBar.setVisible(!APRandomizer.isConnected());

        layerDugBar.setName(Component.literal("Layers Dug (" + layerManager.getFinishedAmount() + "/192)"));

    }

    public void checkGoalCompletion() {
        if(!APRandomizer.isConnected())
            return;

        if(layerManager.getFinishedAmount() >= 192) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }
    }
}
