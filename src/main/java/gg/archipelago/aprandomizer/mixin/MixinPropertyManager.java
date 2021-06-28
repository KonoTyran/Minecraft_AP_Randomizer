package gg.archipelago.aprandomizer.mixin;

import gg.archipelago.aprandomizer.APRandomizer;
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
        LogManager.getLogger().info("Attempting to Inject Seed");
        properties.setProperty("level-seed", ""+APRandomizer.getApmcData().world_seed);

    }
}
