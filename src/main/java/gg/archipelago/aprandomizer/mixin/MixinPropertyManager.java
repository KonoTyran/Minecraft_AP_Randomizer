package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.APClient.APClient;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.APStorage.APMCData;
import net.minecraft.server.dedicated.PropertyManager;
import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.nio.file.Path;
import java.util.Properties;


@Mixin(PropertyManager.class)
public abstract class MixinPropertyManager {

    @Inject(method = "loadFromFile(Ljava/nio/file/Path;)Ljava/util/Properties;", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private static void onLoadFromFile(Path p_218969_0_, CallbackInfoReturnable<Properties> cir, Properties properties) {
        APMCData data = APRandomizer.getApmcData();
        LogManager.getLogger().info("Injecting Archipelago Seed");

        properties.setProperty("level-seed", "" + data.world_seed);
        properties.setProperty("spawn-protection", "0");

        if(data.race) {
            LogManager.getLogger().info("Archipelago race flag found enforcing view-distance of 10");
            properties.setProperty("view-distance", "10");
        }


    }
}