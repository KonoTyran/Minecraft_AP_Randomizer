package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.capability.APCapabilities;
import gg.archipelago.aprandomizer.capability.data.WorldData;
import gg.archipelago.aprandomizer.common.Utils.Utils;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.LayerManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.client.network.client.BouncePacket;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";

    //store our APClient
    static private APClient apClient;

    static public MinecraftServer server;

    static private LayerManager layerManager;
    static private ItemManager itemManager;
    static private GoalManager goalManager;
    static private APMCData apmcData;
    static private final Set<Integer> validVersions = new HashSet<>() {{
        this.add(-1); // april first
    }};
    static private boolean jailPlayers = true;
    static private BlockPos jailCenter = BlockPos.ZERO;
    static private WorldData worldData;
    static private double lastDeathTimestamp;

    public APRandomizer() {
        LOGGER.info("Minecraft Archipelago 1.19.4 v-1 Randomizer initializing.");

        // Register ourselves for server and other game events we are interested in
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.register(this);


        Gson gson = new Gson();
        try {
            Path path = Paths.get("./APData/");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                LOGGER.info("APData folder missing, creating.");
            }

            File[] files = new File(path.toUri()).listFiles((d, name) -> name.endsWith(".apmc"));
            assert files != null;
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            String b64 = Files.readAllLines(files[0].toPath()).get(0);
            String json = new String(Base64.getDecoder().decode(b64));
            apmcData = gson.fromJson(json, APMCData.class);
            if (!validVersions.contains(apmcData.client_version)) {
                apmcData.state = APMCData.State.INVALID_VERSION;
            }
            //LOGGER.info(apmcData.structures.toString());
        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException | AssertionError e) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
            if (apmcData == null) {
                apmcData = new APMCData();
                apmcData.state = APMCData.State.MISSING;
            }
        }

    }

    public static APClient getAP() {
        return apClient;
    }

    public static boolean isConnected() {
        return (apClient != null && apClient.isConnected());
    }

    public static LayerManager getLayerManager() {
        return layerManager;
    }

    public static APMCData getApmcData() {
        return apmcData;
    }

    public static MinecraftServer getServer() {
        return server;
    }

    public static ItemManager getItemManager() {
        return itemManager;
    }

    public static Set<Integer> getValidVersions() {
        return validVersions;
    }


    public static boolean isJailPlayers() {
        return jailPlayers;
    }

    public static void setJailPlayers(boolean jailPlayers) {
        APRandomizer.jailPlayers = jailPlayers;
        worldData.setJailPlayers(jailPlayers);
    }

    public static BlockPos getJailPosition() {
        return jailCenter;
    }

    public static boolean isRace() {
        return getApmcData().race;
    }

    public static void sendBounce(BouncePacket packet) {
        if (apClient != null)
            apClient.sendBounce(packet);
    }

    public static GoalManager getGoalManager() {
        return goalManager;
    }

    public static void setLastDeathTimestamp(double deathTime) {
        lastDeathTimestamp = deathTime;
    }

    public static double getLastDeathTimestamp() {
        return lastDeathTimestamp;
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (apmcData.state != APMCData.State.VALID) {
            LOGGER.error("invalid APMC file");
        }
        server = event.getServer();
    }

    @SuppressWarnings("UnusedAssignment")
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        // do something when the server starts
        layerManager = new LayerManager();
        itemManager = new ItemManager();
        goalManager = new GoalManager();

        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        assert overworld != null;

        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).value = 0;
        server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).onChanged(server);
        server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, server);
        overworld.setDayTime(0);
        server.setDifficulty(Difficulty.NORMAL, true);

        //fetch our custom world save data we attach to the worlds.
        worldData = server.getLevel(Level.OVERWORLD).getCapability(APCapabilities.WORLD_DATA).orElseThrow(AssertionError::new);
        jailPlayers = worldData.getJailPlayers();
        layerManager.setCheckedLayers(worldData.getLocations());


        //check if APMC data is present and if the seed matches what we expect
        if (apmcData.state == APMCData.State.VALID && !worldData.getSeedName().equals(apmcData.seed_name)) {
            //check to see if our worlddata is empty if it is then save the aproom data.
            if (worldData.getSeedName().isEmpty()) {
                worldData.setSeedName(apmcData.seed_name);
                //this is also our first boot so set this flag so we can do first boot stuff.
            } else {
                apmcData.state = APMCData.State.INVALID_SEED;
            }
        }

        //if no apmc file was found set our world data seed to invalid so it will force a regen of this blank world.
        if (apmcData.state == APMCData.State.MISSING) {
            worldData.setSeedName("Invalid");
        }

        if (apmcData.state == APMCData.State.VALID) {
            apClient = new APClient(server);
        }

        if (jailPlayers) {
            for (int x = -5; x <= -1; x++) {
                for (int z = -5; z <= -1; z++) {
                    overworld.setBlock(new BlockPos(x, 128, z), Blocks.BEDROCK.defaultBlockState(), 2);
                }
            }

            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    overworld.setBlock(new BlockPos(x, -64, z), Blocks.BEDROCK.defaultBlockState(), 2);
                }
            }

            //stone
            for (int x = 0; x <= 15; x++) {
                for (int z = 0; z <= 15; z++) {
                    for (int y = -63; y <= 128; y++) {
                        overworld.setBlock(new BlockPos(x, y, z), Blocks.STONE.defaultBlockState(), 2);
                    }
                }
            }

            overworld.setDefaultSpawnPos(new BlockPos(-3, 129, -3), 0f);
            jailCenter = overworld.getSharedSpawnPos();
        }

        if (apmcData.state == APMCData.State.VALID && apmcData.server != null) {

            APClient apClient = APRandomizer.getAP();
            apClient.setName(apmcData.player_name);
            String address = apmcData.server.concat(":" + apmcData.port);

            Utils.sendMessageToAll("Connecting to Archipelago server at " + address);

            try {
                apClient.connect(address);
            } catch (URISyntaxException e) {
                Utils.sendMessageToAll("unable to connect");
            }
        }
    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (apClient != null)
            apClient.close();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        if (apClient != null)
            apClient.close();
    }
}
