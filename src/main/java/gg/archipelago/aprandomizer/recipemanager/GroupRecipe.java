package gg.archipelago.aprandomizer.recipemanager;

import net.minecraft.item.crafting.IRecipe;

import java.util.*;

public class GroupRecipe implements Recipe {
    int id;
    String name;
    String[] namespaceIDs;
    Set<IRecipe<?>> iRecipes = new HashSet<>();

    GroupRecipe(int id, String name, String[] namespaceIDs) {
        this.id = id;
        this.name = name;
        this.namespaceIDs = namespaceIDs;
    }

    protected void addIRecipe(IRecipe<?> iRecipe) {
        this.iRecipes.add(iRecipe);
    }

    public Set<IRecipe<?>> getIRecipes() {
        return iRecipes;
    }

    @Override
    public Set<IRecipe<?>> getGrantedRecipes() {
        return iRecipes;
    }
}
