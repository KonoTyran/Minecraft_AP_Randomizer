package gg.archipelago.aprandomizer.mixin;

import com.google.gson.JsonElement;
import gg.archipelago.aprandomizer.APRandomizer;
import gg.archipelago.aprandomizer.managers.advancementmanager.AdvancementManager;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementList;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

import static gg.archipelago.aprandomizer.APRandomizer.LOGGER;

@Mixin(ServerAdvancementManager.class)
public abstract class MixinAdvancementLoad {

    @Shadow
    public AdvancementList advancements;

    @Inject(method = "apply(Ljava/util/Map;Lnet/minecraft/server/packs/resources/ResourceManager;Lnet/minecraft/util/profiling/ProfilerFiller;)V", at = @At("RETURN"))
    private void loadAdvancements(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler, CallbackInfo ci) {
        LOGGER.debug("Advancement Load Found.");

        advancements.getAllAdvancements().removeIf(
                advancement -> advancement.getId().getPath().startsWith("recipes/")
        );

        Style hardStyle = Style.EMPTY.withBold(true).withColor(TextColor.parseColor("#FFA500"));
        Component hardText = Component.literal(" (Hard)").withStyle(hardStyle);

        var newAdvancements = new HashMap<ResourceLocation, Advancement.Builder>();
        var advIterator = advancements.getAllAdvancements().iterator();
        while (advIterator.hasNext()) {
            var advancement = advIterator.next();
            if(AdvancementManager.hardAdvancements.contains(advancement.getId())) {
                advIterator.remove();
                var display = advancement.getDisplay();
                LOGGER.debug("Hard advancement " + advancement.getDisplay().getTitle().getString() + " found.");
                var title = display.getTitle().copy().append(hardText);


                var newDisplay = new DisplayInfo(display.getIcon(), title, display.getDescription(), display.getBackground(), display.getFrame(), true, false, false);
                var advancementBuilder = advancement.deconstruct().display(newDisplay);
                newAdvancements.put(advancement.getId(), advancementBuilder);
                LOGGER.debug("Hard advancement " + newDisplay.getTitle().getString() + " modified.");
            }
        }

        advancements.add(newAdvancements);

    }
}
