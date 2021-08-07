package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.item.crafting.IRecipe;

import java.util.Set;

public interface Recipe {

    Set<IRecipe<?>> getGrantedRecipes();

}
