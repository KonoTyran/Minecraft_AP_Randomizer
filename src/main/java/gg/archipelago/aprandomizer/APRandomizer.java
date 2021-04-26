package gg.archipelago.aprandomizer;

import com.google.gson.Gson;
import gg.archipelago.APClient.SaveData;
import gg.archipelago.aprandomizer.advancementmanager.AdvancementManager;
import gg.archipelago.aprandomizer.itemmanager.ItemManager;
import gg.archipelago.aprandomizer.recipemanager.RecipeManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.GameRules;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.StructureFeatures;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppedEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.function.Supplier;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("aprandomizer")
public class APRandomizer
{
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //store our APClient
    static private APClient apClient;

    static private MinecraftServer server;

    static private AdvancementManager advancementManager;
    static private RecipeManager recipeManager;
    static private ItemManager itemManager;
    static private APMCData apmcData;
    static private final int[] logicVersion = {0, 2};

    public APRandomizer() {
        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        Gson gson = new Gson();
        try {
            Path path = Paths.get("./APData/");
            File[] files = new File(path.toUri()).listFiles((d,name) -> name.endsWith(".apmc"));
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            String b64 = Files.readAllLines(files[0].toPath()).get(0);
            String json = new String(Base64.getDecoder().decode(b64));
            apmcData = gson.fromJson(json, APMCData.class);

            LOGGER.info(apmcData.structures.toString());

        } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException e) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
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

    public static int[] getLogicVersion() {
        return logicVersion;
    }

    @SubscribeEvent
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        if (apmcData == null) {
            LOGGER.error("no .apmc file found. please place .apmc file in './APData/' folder.");
            event.getServer().close();
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
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
