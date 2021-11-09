package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import gg.archipelago.APClient.network.BouncePacket;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.capability.CapabilityWorldData;
import gg.archipelago.aprandomizer.capability.WorldData;
import gg.archipelago.aprandomizer.managers.GoalManager;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.managers.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.managers.recipemanager.RecipeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmlserverevents.FMLServerAboutToStartEvent;
import net.minecraftforge.fmlserverevents.FMLServerStartingEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppedEvent;
import net.minecraftforge.fmlserverevents.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ArtifactVersion;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer {
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";

    //store our APClient
    static private APClient apClient;

    static private MinecraftServer server;

    static private AdvancementManager advancementManager;
    static private RecipeManager recipeManager;
    static private ItemManager itemManager;
    static private GoalManager goalManager;
    static private APMCData apmcData;
    static private final int clientVersion = 7;
    static private boolean jailPlayers = true;
    static private BlockPos jailCenter = BlockPos.ZERO;
    static private WorldData worldData;
    static private long lastDeathTimestamp;

    public APRandomizer() {
        if (ModList.get().getModContainerById(MODID).isPresent()) {
            LOGGER.info("Minecraft Archipelago v0.2.0 Randomizer initializing.");
        }

        // For registration and init stuff.
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        APStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);
        modEventBus.addListener(this::setup);

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
            if (apmcData.client_version != clientVersion) {
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

    public static AdvancementManager getAdvancementManager() {
        return advancementManager;
    }

    public static RecipeManager getRecipeManager() {
        return recipeManager;
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

    public static int getClientVersion() {
        return clientVersion;
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
        if(apClient != null)
            apClient.sendBounce(packet);
    }

    public static GoalManager getGoalManager() {
        return goalManager;
    }

    public static void setLastDeathTimestamp(long deathTime) {
        lastDeathTimestamp = deathTime;
    }

    public static long getLastDeathTimestamp() {
        return lastDeathTimestamp;
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        if (apmcData.state != APMCData.State.VALID) {
            LOGGER.error("invalid APMC file");
        }
    }

    /**
     * Here, setupStructures will be ran after registration of all structures are finished.
     * This is important to be done here so that the Deferred Registry has already ran and
     * registered/created our structure for us.
     * <p>
     * Once after that structure instance is made, we then can now do the rest of the setup
     * that requires a structure instance such as setting the structure spacing, creating the
     * configured structure instance, and more.
     */
    public void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            APStructures.setupStructures();
            APConfiguredStructures.registerConfiguredStructures();
        });
    }


    @SuppressWarnings("UnusedAssignment")
    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

        // do something when the server starts
        server = event.getServer();
        advancementManager = new AdvancementManager();
        recipeManager = new RecipeManager();
        itemManager = new ItemManager();
        goalManager = new GoalManager();


        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true, server);
        server.getGameRules().getRule(GameRules.RULE_ANNOUNCE_ADVANCEMENTS).set(false, server);
        server.setDifficulty(Difficulty.NORMAL, true);

        //fetch our custom world save data we attach to the worlds.
        worldData = server.getLevel(Level.OVERWORLD).getCapability(CapabilityWorldData.CAPABILITY_WORLD_DATA).orElseThrow(AssertionError::new);
        jailPlayers = worldData.getJailPlayers();
        advancementManager.setCheckedAdvancements(worldData.getLocations());


        //check if APMC data is present and if the seed matches what we expect
        if (apmcData.state == APMCData.State.VALID && !worldData.getSeedName().equals(apmcData.seed_name)) {
            //check to see if our worlddata is empty if it is then save the aproom data.
            if (worldData.getSeedName().isEmpty()) {
                worldData.setSeedName(apmcData.seed_name);
                //this is also our first boot so set this flag so we can do first boot stuff.
            }
            else {
                apmcData.state = APMCData.State.INVALID_SEED;
            }
        }

        //if no apmc file was found set our world data seed to invalid so it will force a regen of this blank world.
        if (apmcData.state == APMCData.State.MISSING) {
            worldData.setSeedName("Invalid");
        }

        if(apmcData.state == APMCData.State.VALID) {
            apClient = new APClient(server);
        }


        //preload the nether so that fetching of structures works.
        ServerLevel nether = server.getLevel(Level.NETHER);
        assert nether != null;

        //check to see if the chunk is loaded then fetch/generate if it is not.
        if (!nether.hasChunk(0, 0)) { //Chunk is unloaded
            ChunkAccess chunk = nether.getChunk(0, 0, ChunkStatus.EMPTY, true);
            if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
                chunk = nether.getChunk(0, 0, ChunkStatus.FULL);
            }
        }

        ServerLevel theEnd = server.getLevel(Level.END);
        assert theEnd != null;

        //check to see if the chunk is loaded then fetch/generate if it is not.
        if (!theEnd.hasChunk(0, 0)) { //Chunk is unloaded
            ChunkAccess chunk = theEnd.getChunk(0, 0, ChunkStatus.EMPTY, true);
            if (!chunk.getStatus().isOrAfter(ChunkStatus.FULL)) {
                chunk = theEnd.getChunk(0, 0, ChunkStatus.FULL);
            }
        }
        //check if there is dragon data, if not create new stuff.
        if (theEnd.dragonFight == null)
            theEnd.dragonFight = new EndDragonFight(theEnd, server.getWorldData().worldGenSettings().seed(), server.getWorldData().endDragonFightData());
        //spawn 20 end gateways spawnNewGateway will do nothing if they are all already spawned.
        for (int i = 0; i < 20; i++) {
            theEnd.dragonFight.spawnNewGateway();
        }
        if (theEnd.dragonFight.portalLocation == null || theEnd.dragonFight.portalLocation.getY() == -1) {
            //get the top block of 0,0 then spawn the portal there, the parameter is whether or not to make it an active portal
            BlockPos pos = theEnd.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, new BlockPos(0, 255, 0));
            theEnd.dragonFight.portalLocation = pos.below();
        }
        theEnd.dragonFight.spawnExitPortal(theEnd.dragonFight.dragonKilled);
        theEnd.save(null, true, false);
        //theEnd.getServer().getWorldData().setEndDragonFightData(theEnd.dragonFight().saveData());


        if(jailPlayers) {
            ServerLevel overworld = server.getLevel(Level.OVERWORLD);
            BlockPos spawn = overworld.getSharedSpawnPos();
            BlockPos jailPos = new BlockPos(spawn.getX(), 240, spawn.getZ());
            StructureTemplate jail = overworld.getStructureManager().get(new ResourceLocation(MODID,"spawnjail")).get();
            jailCenter = new BlockPos(jailPos.getX() + (jail.getSize().getX()/2),jailPos.getY() + 1, jailPos.getZ() + (jail.getSize().getZ()/2));
            jail.placeInWorld(overworld,jailPos,jailPos,new StructurePlaceSettings(),new Random(),2);
            server.getGameRules().getRule(GameRules.RULE_DAYLIGHT).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_WEATHER_CYCLE).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_DOFIRETICK).set(false, server);
            server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).value = 0;
            server.getGameRules().getRule(GameRules.RULE_RANDOMTICKING).onChanged(server);
            server.getGameRules().getRule(GameRules.RULE_DO_PATROL_SPAWNING).set(false,server);
            server.getGameRules().getRule(GameRules.RULE_DO_TRADER_SPAWNING).set(false,server);
            server.getGameRules().getRule(GameRules.RULE_MOBGRIEFING).set(false,server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBSPAWNING).set(false,server);
            server.getGameRules().getRule(GameRules.RULE_DO_IMMEDIATE_RESPAWN).set(true,server);
            server.getGameRules().getRule(GameRules.RULE_DOMOBLOOT).set(false,server);
            server.getGameRules().getRule(GameRules.RULE_DOENTITYDROPS).set(false,server);
            overworld.setDayTime(0);

        }

    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        if(apClient != null)
            apClient.close();
    }

    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event) {
        if(apClient != null)
            apClient.close();
    }
}
