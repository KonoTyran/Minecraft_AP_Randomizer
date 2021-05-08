package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.capability.CapabilityPlayerData;
import gg.archipelago.aprandomizer.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.recipemanager.RecipeManager;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(APRandomizer.MODID)
public class APRandomizer
{
    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "aprandomizer";

    //store our APClient
    static private APClient apClient;

    static private MinecraftServer server;

    static private AdvancementManager advancementManager;
    static private RecipeManager recipeManager;
    static private ItemManager itemManager;
    static private APMCData apmcData;
    static private final int[] clientVersion = {0, 3};

    public APRandomizer() {

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
            File[] files = new File(path.toUri()).listFiles((d,name) -> name.endsWith(".apmc"));
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            String b64 = Files.readAllLines(files[0].toPath()).get(0);
            String json = new String(Base64.getDecoder().decode(b64));
            apmcData = gson.fromJson(json, APMCData.class);
            if(!Arrays.equals(apmcData.client_version,clientVersion)) {
                apmcData.state = APMCData.State.INVALID_VERSION;
            }
            //LOGGER.info(apmcData.structures.toString());

        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
            if(apmcData == null) {
                apmcData = new APMCData();
                apmcData.state = APMCData.State.MISSING;
            }
        }
    }

    public static APClient getAP() {
        return apClient;
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

    public static int[] getClientVersion() {
        return clientVersion;
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        if (apmcData == null) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
            //event.getServer().close();
            return;
        }


        // todo figure out how to erase world and gen with this seed....
        if(event.getServer().getWorldData().worldGenSettings().seed() != apmcData.world_seed) {
            //event.getServer().getWorldPath(new FolderName(event.getServer().getWorldData().getLevelName()));

        }

    }

    /**
     * Here, setupStructures will be ran after registration of all structures are finished.
     * This is important to be done here so that the Deferred Registry has already ran and
     * registered/created our structure for us.
     *
     * Once after that structure instance is made, we then can now do the rest of the setup
     * that requires a structure instance such as setting the structure spacing, creating the
     * configured structure instance, and more.
     */
    public void setup(final FMLCommonSetupEvent event)
    {
        CapabilityPlayerData.register();

        event.enqueueWork(() -> {
            APStructures.setupStructures();
            APConfiguredStructures.registerConfiguredStructures();
        });
    }


    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {

        // do something when the server starts
        server = event.getServer();
        apClient = new APClient(server);
        advancementManager = new AdvancementManager();
        recipeManager = new RecipeManager();
        itemManager = new ItemManager();

        server.getGameRules().getRule(GameRules.RULE_LIMITED_CRAFTING).set(true,server);
        server.getGameRules().getRule(GameRules.RULE_KEEPINVENTORY).set(true,server);
        server.setDifficulty(Difficulty.NORMAL,true);
        ServerWorld theEnd = server.getLevel(World.END);
        assert theEnd != null;
        for (EnderDragonEntity dragon : theEnd.getDragons()) {
            dragon.remove();
        }

    }

    @SubscribeEvent
    public void onServerStopping(FMLServerStoppingEvent event) {
        apClient.close();
    }
    @SubscribeEvent
    public void onServerStopped(FMLServerStoppedEvent event) {
        apClient.close();
    }
}
