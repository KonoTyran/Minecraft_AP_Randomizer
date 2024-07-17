package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.HashSet;
import java.util.Set;

public class GroupRecipe implements APRecipe {
    int id;
    String name;
    String[] namespaceIDs;
    Set<RecipeHolder<?>> iRecipes = new HashSet<>();

    GroupRecipe(int id, String name, String[] namespaceIDs) {
        this.id = id;
        this.name = name;
        this.namespaceIDs = namespaceIDs;
    }

    protected void addIRecipe(RecipeHolder<?> iRecipe) {
        this.iRecipes.add(iRecipe);
    }

    public Set<RecipeHolder<?>> getIRecipes() {
        return iRecipes;
    }

    @Override
    public Set<RecipeHolder<?>> getGrantedRecipes() {
        return iRecipes;
    }
}
