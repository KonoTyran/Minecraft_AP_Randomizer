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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
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
    static public WorldData worldData;
    static private double lastDeathTimestamp;

    public APRandomizer() {
        LOGGER.info("Minecraft Archipelago 1.19.4 version (-1) Randomizer initializing.");

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
        //return true;
        return getApmcData().race;
    }

    public static GoalManager getGoalManager() {
        return goalManager;
    }

    @SubscribeEvent
    public void onServerAboutToStart(ServerAboutToStartEvent event) {
        if (apmcData.state != APMCData.State.VALID) {
            LOGGER.error("invalid APMC file");
        }
        server = event.getServer();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

        // do something when the server starts
        layerManager = new LayerManager();
        itemManager = new ItemManager();
        goalManager = new GoalManager();

        ServerLevel overworld = server.overworld();

        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_FALL_DAMAGE).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(false, server);
        server.getGameRules().getRule(GameRules.RULE_DOBLOCKDROPS).set(false, server);
        overworld.setDayTime(0);
        server.setDifficulty(Difficulty.NORMAL, true);

        //fetch our custom world save data we attach to the worlds.
        worldData = overworld.getCapability(APCapabilities.WORLD_DATA).orElseThrow(AssertionError::new);
        jailPlayers = worldData.getJailPlayers();
        layerManager.setCheckedLayers(new HashSet<>(worldData.getLocations()));


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
            if(!server.getScoreboard().hasObjective("deaths")) {
                Objective deaths = server.getScoreboard().addObjective("deaths", ObjectiveCriteria.DEATH_COUNT, Component.literal("deaths"), ObjectiveCriteria.RenderType.INTEGER);
                server.getScoreboard().setDisplayObjective(Scoreboard.DISPLAY_SLOT_SIDEBAR, deaths);
            }

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

            StructureTemplate endChunk = overworld.getStructureManager().get(new ResourceLocation(MODID,"end1")).get();
            BlockPos endChunkPos = new BlockPos(0,-63,0);
            endChunk.placeInWorld(overworld,endChunkPos,endChunkPos,new StructurePlaceSettings(), RandomSource.create(),2);

            StructureTemplate netherChunk = overworld.getStructureManager().get(new ResourceLocation(MODID,"nether1")).get();
            BlockPos netherChunkPos = new BlockPos(0,-15,0);
            netherChunk.placeInWorld(overworld,netherChunkPos,netherChunkPos,new StructurePlaceSettings(), RandomSource.create(),2);

            StructureTemplate overworld1Chunk = overworld.getStructureManager().get(new ResourceLocation(MODID,"overworld1")).get();
            BlockPos overworld1ChunkPos = new BlockPos(0,33,0);
            overworld1Chunk.placeInWorld(overworld,overworld1ChunkPos,overworld1ChunkPos,new StructurePlaceSettings(), RandomSource.create(),2);

            StructureTemplate overworld2Chunk = overworld.getStructureManager().get(new ResourceLocation(MODID,"overworld2")).get();
            BlockPos overworld2ChunkPos = new BlockPos(0,81,0);
            overworld2Chunk.placeInWorld(overworld,overworld2ChunkPos,overworld2ChunkPos,new StructurePlaceSettings(), RandomSource.create(),2);

            overworld.setDefaultSpawnPos(new BlockPos(-3, 129, -3), 0f);
            jailCenter = new BlockPos(0,127,0);

            WorldBorder border = overworld.getWorldBorder();
            border.setCenter(ServerLevel.END_SPAWN_POINT.getX()+.5,ServerLevel.END_SPAWN_POINT.getZ()+.5);
            border.setSize(5);
            border.setWarningBlocks(0);
            border.setWarningTime(0);
            border.setDamageSafeZone(0);
            border.setDamagePerBlock(Double.MAX_VALUE);

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
