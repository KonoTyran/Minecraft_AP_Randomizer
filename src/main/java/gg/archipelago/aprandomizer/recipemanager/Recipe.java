package gg.archipelago.aprandomizer.recipemanager;

import net.minecraft.item.crafting.IRecipe;

import java.util.Set;

public interface Recipe {

    public Set<IRecipe<?>> getGrantedRecipes();

}
