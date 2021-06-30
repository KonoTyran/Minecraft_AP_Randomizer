package gg.archipelago.aprandomizer.recipemanager;

import net.minecraft.item.crafting.IRecipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgressiveRecipe implements Recipe {

    int id;
    String name;
    ArrayList<String[]> namespaceIDs;
    List<Set<IRecipe<?>>> recipes = new ArrayList<>();
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

    protected void addIRecipe(IRecipe<?> iRecipe, int tier) {
        this.recipes.get(tier).add(iRecipe);
    }

    public int getCurrentTier() {
        return currentTier;
    }

    public void setCurrentTier(int tier) {
        this.currentTier = tier;
    }

    public Set<IRecipe<?>> getTier(int tier) {
        if (recipes.size() >= tier)
            return recipes.get(tier - 1);
        return recipes.get(recipes.size() - 1);
    }


    @Override
    public Set<IRecipe<?>> getGrantedRecipes() {
        return getTier(++currentTier);
    }
}
