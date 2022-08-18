package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import net.minecraft.server.dedicated.Settings;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Properties;


@Mixin(Settings.class)
public abstract class MixinPropertyManager {

    @Inject(method = "loadFromFile(Ljava/nio/file/Path;)Ljava/util/Properties;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onLoadFromFile(Path p_218969_0_, CallbackInfoReturnable<Properties> cir, Properties properties) {
        APMCData data = APRandomizer.getApmcData();
        LogManager.getLogger().info("Injecting Archipelago Seed");

        properties.setProperty("level-seed", "" + data.world_seed);
        properties.setProperty("spawn-protection", "0");
        properties.setProperty("allow-flight", "true");
        properties.setProperty("level-name", "Archipelago-" + data.seed_name + "-P" + data.player_id);

        if (data.dig_chunk) {
            properties.setProperty("level-type", "flat");
            properties.setProperty("generator-settings", "{\"layers\":[{\"block\":\"minecraft:air\",\"height\":319}],\"biome\":\"minecraft:the_void\"}");
        } else {
            properties.setProperty("level-type", "default");
            properties.setProperty("generator-settings", "{}");
        }

        if(data.race) {
            LogManager.getLogger().info("Archipelago race flag found enforcing race settings.");
            properties.setProperty("view-distance", "10");
            properties.setProperty("gamemode", "survival");
            properties.setProperty("force-gamemode", "true");
        }


    }
}
