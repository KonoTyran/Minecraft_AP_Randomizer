package gg.archipelago.aprandomizer.managers.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.crafting.Recipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class RecipeManager {

    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    //have a lookup of every advancement
    private final RecipeData recipeData;

    private final Set<Recipe<?>> initialRestricted = new HashSet<>();
    private final Set<Recipe<?>> initialGranted = new HashSet<>();

    private Set<Recipe<?>> restricted = new HashSet<>();
    private Set<Recipe<?>> granted = new HashSet<>();

    private Set<ResourceLocation> itemAdvancements = new HashSet<>();


    public RecipeManager() {
        recipeData = new RecipeData();
        Collection<Recipe<?>> recipeList = APRandomizer.getServer().getRecipeManager().getRecipes();
        for (Recipe<?> iRecipe : recipeList) {
            if (recipeData.injectIRecipe(iRecipe)) {
                initialRestricted.add(iRecipe);
            } else {
                initialGranted.add(iRecipe);
            }
        }
        restricted = initialRestricted;
        granted = initialGranted;
    }

    public void grantRecipeList(List<Long> recipes) {
        for (var id : recipes) {
            if (!recipeData.hasID(id))
                continue;
            grantRecipe(id);
            return;
        }
    }

    public void grantRecipe(long id) {
        if (!recipeData.hasID(id))
            return;
        Set<Recipe<?>> toGrant = recipeData.getID(id).getGrantedRecipes();

        granted.addAll(toGrant);
        restricted.removeAll(toGrant);
        itemAdvancements.addAll(recipeData.getID(id).getUnlockedTrackingAdvanements());

        for (ServerPlayer player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            //player.resetRecipes(restricted);
            player.awardRecipes(granted);

            var serverAdvancements = APRandomizer.getServer().getAdvancements();
            recipeData.getID(id).getUnlockedTrackingAdvanements().forEach(
                    location -> {
                        var trackingAdvancement = serverAdvancements.getAdvancement(location);
                        if (trackingAdvancement != null) {
                            APRandomizer.getAdvancementManager().syncAdvancement(trackingAdvancement);
                        }

                    });
        }
    }

    public Set<Recipe<?>> getRestrictedRecipes() {
        return restricted;
    }

    public Set<Recipe<?>> getGrantedRecipes() {
        return granted;
    }

    public void resetRecipes() {
        restricted = initialRestricted;
        granted = initialGranted;
        recipeData.reset();
    }

    public boolean hasReceived(ResourceLocation id) {
        return itemAdvancements.contains(id);
    }
}
