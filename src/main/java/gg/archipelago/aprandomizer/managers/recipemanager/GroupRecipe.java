package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.world.item.crafting.Recipe;

import java.util.HashSet;
import java.util.Set;

public class GroupRecipe implements APRecipe {
    int id;
    String name;
    String[] namespaceIDs;
    Set<Recipe<?>> iRecipes = new HashSet<>();

    GroupRecipe(int id, String name, String[] namespaceIDs) {
        this.id = id;
        this.name = name;
        this.namespaceIDs = namespaceIDs;
    }

    protected void addIRecipe(Recipe<?> iRecipe) {
        this.iRecipes.add(iRecipe);
    }

    public Set<Recipe<?>> getIRecipes() {
        return iRecipes;
    }

    @Override
    public Set<Recipe<?>> getGrantedRecipes() {
        return iRecipes;
    }
}
