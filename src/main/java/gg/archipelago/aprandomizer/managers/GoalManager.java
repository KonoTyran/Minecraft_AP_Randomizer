package gg.archipelago.aprandomizer.managers;

import dev.koifysh.archipelago.ClientStatus;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.ap.storage.APMCData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.data.WorldData;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.bossevents.CustomBossEvent;
import net.minecraft.server.bossevents.CustomBossEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class GoalManager {

    int advancementsRequired = 0;

    int dragonEggShardsRequired = 0;

    int totalDragonEggShards = 0;

    private final AdvancementManager advancementManager;

    private CustomBossEvent advancementInfoBar;
    private CustomBossEvent eggInfoBar;
    private CustomBossEvent connectionInfoBar;

    private APMCData apmc;

    private boolean dragonKilled = false;
    private boolean witherKilled = false;

    public GoalManager () {
        apmc = APRandomizer.getApmcData();
        advancementManager = APRandomizer.getAdvancementManager();
        advancementsRequired = apmc.advancements_required;
        dragonEggShardsRequired = apmc.egg_shards_required;
        totalDragonEggShards = apmc.egg_shards_available;
        initializeInfoBar();
    }

    public void initializeInfoBar() {
        CustomBossEvents bossInfoManager = APRandomizer.getServer().getCustomBossEvents();
        advancementInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"advancementinfobar"), Component.literal(""));
        advancementInfoBar.setMax(advancementsRequired);
        advancementInfoBar.setColor(BossEvent.BossBarColor.PINK);
        advancementInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_10);

        eggInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"egginfobar"), Component.literal(""));
        eggInfoBar.setMax(dragonEggShardsRequired);
        eggInfoBar.setColor(BossEvent.BossBarColor.WHITE);
        eggInfoBar.setOverlay(BossEvent.BossBarOverlay.NOTCHED_6);

        connectionInfoBar = bossInfoManager.create(new ResourceLocation(APRandomizer.MODID,"connectioninfobar"), Component.literal("Not connected to Archipelago").withStyle(Style.EMPTY.withColor(TextColor.parseColor("red"))));
        connectionInfoBar.setMax(1);
        connectionInfoBar.setValue(1);
        connectionInfoBar.setColor(BossEvent.BossBarColor.RED);
        connectionInfoBar.setOverlay(BossEvent.BossBarOverlay.PROGRESS);

        updateInfoBar();
        advancementInfoBar.setVisible((advancementsRequired > 0));
        eggInfoBar.setVisible((dragonEggShardsRequired > 0));
        connectionInfoBar.setVisible(true);
    }

    public void updateGoal(boolean canFinish) {
        updateInfoBar();
        if(canFinish)
            checkGoalCompletion();
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
        for (var item : APRandomizer.getItemManager().getAllItems()) {
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

        advancementInfoBar.setName(Component.literal(getAdvancementRemainingString()));
        eggInfoBar.setName(Component.literal(getEggShardsRemainingString()));

    }

    public void checkGoalCompletion() {
        if(!APRandomizer.isConnected())
            return;
        //check to see if our goal is done! if so send compleatoin to AP server
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.NONE) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check if both bosses have/need to be killed
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.BOTH && dragonKilled && witherKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check wither goal completion
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.WITHER && witherKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }

        //check wither goal completion
        if(goalsDone() && apmc.required_bosses == APMCData.Bosses.ENDER_DRAGON && dragonKilled) {
            APRandomizer.getAP().setGameState(ClientStatus.CLIENT_GOAL);
        }
    }

    public void checkDragonSpawn() {
        WorldData worldData = APRandomizer.getWorldData();

        //check if the dragon is not spawned and we need to spawn it.
        if (goalsDone() && worldData.getDragonState() == WorldData.DRAGON_ASLEEP) {
            //set the dragon state to spawn as soon as the end 0,0 chunk is loaded
            worldData.setDragonState(WorldData.DRAGON_WAITING);
            Utils.PlaySoundToAll(SoundEvents.ENDER_DRAGON_AMBIENT);
            Utils.sendMessageToAll("The Dragon is waiting...");
            Utils.sendTitleToAll(Component.literal("The Dragon").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(java.awt.Color.ORANGE.getRGB()))), Component.literal("is waiting..."), 40, 120, 40);
        }
    }

    public boolean goalsDone() {
        return advancementManager.getFinishedAmount() >= advancementsRequired && this.currentEggShards() >= dragonEggShardsRequired;
    }


    //subscribe to living death event to check for wither/dragon kills;
    @SubscribeEvent
    static void onBossDeath(LivingDeathEvent event) {
        LivingEntity mob = event.getEntity();
        GoalManager goalManager = APRandomizer.getGoalManager();
        if(mob instanceof EnderDragon && goalManager.goalsDone()) {
            goalManager.dragonKilled = true;
            Utils.sendMessageToAll("She is no more...");
            goalManager.updateGoal(false);
        }
        if(mob instanceof WitherBoss && goalManager.goalsDone()) {
            goalManager.witherKilled = true;
            Utils.sendMessageToAll("The Darkness has lifted...");
            goalManager.updateGoal(true);
        }
    }

    public boolean isDragonDead() {
        return dragonKilled;
    }
    public boolean isWitherDead() {
        return witherKilled;
    }
}
