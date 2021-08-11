package gg.archipelago.aprandomizer.managers;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.server.CustomServerBossInfo;
import net.minecraft.server.CustomServerBossInfoManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.world.BossInfo;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class GoalManager {

    int advancementsRequired = 0;

    int dragonEggShardsRequired = 0;

    int totalDragonEggShards = 0;

    private final AdvancementManager advancementManager;

    private CustomServerBossInfo advancementInfoBar;
    private CustomServerBossInfo eggInfoBar;
    private CustomServerBossInfo connectionInfoBar;

    public GoalManager () {
        APMCData apmcData = APRandomizer.getApmcData();
        advancementManager = APRandomizer.getAdvancementManager();
        advancementsRequired = apmcData.advancements_required;
        dragonEggShardsRequired = apmcData.egg_shards_required;
        totalDragonEggShards = apmcData.egg_shards_available;
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        CustomServerBossInfoManager bossInfoManager = APRandomizer.getServer().getCustomBossEvents();
        advancementInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"advancementinfobar"), new StringTextComponent(""));
        advancementInfoBar.setMax(advancementsRequired);
        advancementInfoBar.setColor(BossInfo.Color.PINK);
        advancementInfoBar.setOverlay(BossInfo.Overlay.NOTCHED_10);

        eggInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"egginfobar"), new StringTextComponent(""));
        eggInfoBar.setMax(dragonEggShardsRequired);
        eggInfoBar.setColor(BossInfo.Color.WHITE);
        eggInfoBar.setOverlay(BossInfo.Overlay.NOTCHED_6);

        connectionInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"connectioninfobar"), new StringTextComponent("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(Color.parseColor("red"))));
        connectionInfoBar.setMax(1);
        connectionInfoBar.setValue(1);
        connectionInfoBar.setColor(BossInfo.Color.RED);
        connectionInfoBar.setOverlay(BossInfo.Overlay.PROGRESS);

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

        advancementInfoBar.setName(new StringTextComponent(getAdvancementRemainingString()));
        eggInfoBar.setName(new StringTextComponent(getEggShardsRemainingString()));

    }

    public void checkDragonSpawn() {
        if (isDone()) {
            ServerWorld end = APRandomizer.getServer().getLevel(World.END);
            assert end != null;
            assert end.dragonFight != null;
            WorldData worldData = end.getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);

            if (worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
                Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
                Utils.sendMessageToAll("The Dragon has awoken.");
                Utils.sendTitleToAll(new StringTextComponent("Ender Dragon").withStyle(Style.EMPTY.withColor(Color.fromRgb(java.awt.Color.ORANGE.getRGB()))), new StringTextComponent("has been awoken"), 40, 120, 40);
                worldData.setDragonState(WorldData.DRAGON_SPAWNED);
                Utils.SpawnDragon(end);
            }
        }
    }

    public boolean isDone() {
        return advancementManager.getFinishedAmount() >= advancementsRequired && this.currentEggShards() >= dragonEggShardsRequired;
    }
}
