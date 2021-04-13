package gg.archipelago.aprandomizer.recipemanager;

import gg.archipelago.aprandomizer.APRandomizer;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.IRecipe;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


public class RecipeManager {

        // Directly reference a log4j logger.
        private static final Logger LOGGER = LogManager.getLogger();

        //have a lookup of every advancement
        private final RecipeData recipeData;

        private Set<IRecipe<?>> restricted = new HashSet<>();
        private Set<IRecipe<?>> granted = new HashSet<>();



        public RecipeManager() {
                recipeData = new RecipeData();
                Collection<IRecipe<?>> recipeList = APRandomizer.getServer().getRecipeManager().getRecipes();
                for (IRecipe<?> iRecipe : recipeList) {
                        if(recipeData.injectIRecipe(iRecipe)) {
                                restricted.add(iRecipe);
                        }
                        else {
                                granted.add(iRecipe);
                        }
                }
        }

        public boolean grantRecipe(int id) {
                if(!recipeData.hasID(id))
                        return false;
                Set<IRecipe<?>> toGrant = recipeData.getID(id).getGrantedRecipes();
                for (ServerPlayerEntity player : APRandomizer.getServer().getPlayerList().getPlayers()) {
                        player.awardRecipes(toGrant);
                        granted.addAll(toGrant);
                        restricted.removeAll(toGrant);
                }
                return true;
        }

        public Set<IRecipe<?>> getRestrictedRecipes() {
                return restricted;
        }

        public Set<IRecipe<?>> getGrantedRecipes() {
                return granted;
        }
}
