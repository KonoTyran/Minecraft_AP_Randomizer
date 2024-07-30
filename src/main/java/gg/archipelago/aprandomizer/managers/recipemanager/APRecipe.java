package gg.archipelago.aprandomizer.managers.recipemanager;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;

import java.util.Set;

public interface APRecipe {

    Set<Recipe<?>> getGrantedRecipes();

    Set<ResourceLocation> getUnlockedTrackingAdvanements();
}
