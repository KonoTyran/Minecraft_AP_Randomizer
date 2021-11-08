package gg.archipelago.aprandomizer.managers;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.Level;

public class GoalManager {

    int advancementsRequired = 0;

    int dragonEggShardsRequired = 0;

    int totalDragonEggShards = 0;

    private final AdvancementManager advancementManager;

    private CustomBossEvent advancementInfoBar;
    private CustomBossEvent eggInfoBar;
    private CustomBossEvent connectionInfoBar;

    public GoalManager () {
        APMCData apmcData = APRandomizer.getApmcData();
        advancementManager = APRandomizer.getAdvancementManager();
        advancementsRequired = apmcData.advancements_required;
        dragonEggShardsRequired = apmcData.egg_shards_required;
        totalDragonEggShards = apmcData.egg_shards_available;
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        CustomBossEvents bossInfoManager = APRandomizer.getServer().getCustomBossEvents();
        advancementInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"advancementinfobar"), new TextComponent(""));
        advancementInfoBar.setMax(advancementsRequired);
        advancementInfoBar.setColor(BossEvent.BossBarColor.PINK);
        advancementInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);

        eggInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"egginfobar"), new TextComponent(""));
        eggInfoBar.setMax(dragonEggShardsRequired);
        eggInfoBar.setColor(BossEvent.BossBarColor.WHITE);
        eggInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);

        connectionInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"connectioninfobar"), new TextComponent("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red"))));
        connectionInfoBar.setMax(1);
        connectionInfoBar.setValue(1);
        connectionInfoBar.setColor(BossEvent.BossBarColor.RED);
        connectionInfoBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);

        updateInfoBar();
        advancementInfoBar.setVisible((advancementsRequired > 0));
        eggInfoBar.setVisible((dragonEggShardsRequired > 0));
        connectionInfoBar.setVisible(true);
    }

    public void updateGoal() {
        updateInfoBar();
        checkDragonSpawn();
    }



    public String getAdvancementRemainingString() {
        if (advancementsRequired > 0) {
            return String.format(" Advancements (%d / %d)", advancementManager.getFinishedAmount(), advancementsRequired);
        }
        return "";
    }

    public String getEggShardsRemainingString() {
        if(dragonEggShardsRequired > 0) {
            return String.format(" Dragon Egg Shards (%d / %d)",currentEggShards(), dragonEggShardsRequired);
        }
        return "";
    }

    private int currentEggShards() {
        int current = 0;
        for (Integer item : APRandomizer.getItemManager().getAllItems()) {
            if(item == ItemManager.DRAGON_EGG_SHARD) {
                ++current;
            }
        }
        return current;
    }

    public void updateInfoBar() {
        if(advancementInfoBar == null || eggInfoBar == null)
            return;
        APRandomizer.getServer().execute(() -> {
            advancementInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
            eggInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
            connectionInfoBar.setPlayers(APRandomizer.getServer().getPlayerList().getPlayers());
        });

        advancementInfoBar.setValue(advancementManager.getFinishedAmount());
        eggInfoBar.setValue(currentEggShards());

        connectionInfoBar.setVisible(!APRandomizer.isConnected());

        advancementInfoBar.setName(new TextComponent(getAdvancementRemainingString()));
        eggInfoBar.setName(new TextComponent(getEggShardsRemainingString()));

    }

    public void checkDragonSpawn() {
        if (isDone()) {
            ServerLevel end = APRandomizer.getServer().getLevel(Level.END);
            assert end != null;
            assert end.dragonFight != null;
            WorldData worldData = end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);

            if (worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
                Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
                Utils.sendMessageToAll("The Dragon has awoken.");
                Utils.sendTitleToAll(new TextComponent("Ender Dragon").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.ORANGE.getRGB()))), new TextComponent("has been awoken"), 40, 120, 40);
                worldData.setDragonState(WorldData.DRAGON_SPAWNED);
                Utils.SpawnDragon(end);
            }
        }
    }

    public boolean isDone() {
        return advancementManager.getFinishedAmount() >= advancementsRequired && this.currentEggShards() >= dragonEggShardsRequired;
    }
}
