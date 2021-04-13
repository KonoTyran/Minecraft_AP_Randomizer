package gg.archipelago.aprandomizer.common.events;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

@Mod.EventBusSubscriber
public class onJoin {
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    static void onPlayerLoginEvent(PlayerEvent.PlayerLoggedInEvent event) {
        APRandomizer.getAdvancementManager().syncAllAdvancements();
        Set<IRecipe<?>> restricted = APRandomizer.getRecipeManager().getRestrictedRecipes();
        Set<IRecipe<?>> granted = APRandomizer.getRecipeManager().getGrantedRecipes();
        event.getPlayer().awardRecipes(granted);
        event.getPlayer().resetRecipes(restricted);
    }
}
