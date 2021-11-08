package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.world.item.crafting.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressiveRecipe implements APRecipe {

    int id;
    String name;
    ArrayList<String[]> namespaceIDs;
    List<Set<Recipe<?>>> recipes = new ArrayList<>();
    int currentTier = 0;

    ProgressiveRecipe(int id, String name, ArrayList<String[]> namespaceIDs) {
        super();
        this.id = id;
        this.name = name;
        this.namespaceIDs = namespaceIDs;
        for (int i = 0; i < namespaceIDs.size(); i++) {
            recipes.add(new HashSet<>());
        }
    }

    protected void addIRecipe(Recipe<?> iRecipe, int tier) {
        this.recipes.get(tier).add(iRecipe);
    }

    public int getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(int tier) {
        this.currentTier = tier;
    }

    public Set<Recipe<?>> getTier(int tier) {
        if (recipes.size() >= tier)
            return recipes.get(tier - 1);
        return recipes.get(recipes.size() - 1);
    }


    @Override
    public Set<Recipe<?>> getGrantedRecipes() {
        return getTier(++currentTier);
    }
}
