package gg.archipelago.aprandomizer.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
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
    private RecipeData recipeData;

    private final Set<IRecipe<?>> initialRestricted = new HashSet<>();
    private final Set<IRecipe<?>> initialGranted = new HashSet<>();

    private Set<IRecipe<?>> restricted = new HashSet<>();
    private Set<IRecipe<?>> granted = new HashSet<>();


    public RecipeManager() {
        recipeData = new RecipeData();
        Collection<IRecipe<?>> recipeList = APRandomizer.getServer().getRecipeManager().getRecipes();
        for (IRecipe<?> iRecipe : recipeList) {
            if (recipeData.injectIRecipe(iRecipe)) {
                initialRestricted.add(iRecipe);
            } else {
                initialGranted.add(iRecipe);
            }
        }
        restricted = initialRestricted;
        granted = initialGranted;
    }

    public boolean grantRecipeList(List<Integer> recipes) {
        for (Integer id : recipes) {
            if (!recipeData.hasID(id))
                continue;
            Set<IRecipe<?>> toGrant = recipeData.getID(id).getGrantedRecipes();
            granted.addAll(toGrant);
            restricted.removeAll(toGrant);
        }
        for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            player.resetRecipes(restricted);
            player.awardRecipes(granted);

        }
        return true;
    }

    public void grantRecipe(int id) {
        if (!recipeData.hasID(id))
            return;
        Set<IRecipe<?>> toGrant = recipeData.getID(id).getGrantedRecipes();

        granted.addAll(toGrant);
        restricted.removeAll(toGrant);

        for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
            //player.resetRecipes(restricted);
            player.awardRecipes(granted);
        }
    }

    public Set<IRecipe<?>> getRestrictedRecipes() {
        return restricted;
    }

    public Set<IRecipe<?>> getGrantedRecipes() {
        return granted;
    }

    public void resetRecipes() {
        restricted = initialRestricted;
        granted = initialGranted;
        recipeData.reset();
    }
}
