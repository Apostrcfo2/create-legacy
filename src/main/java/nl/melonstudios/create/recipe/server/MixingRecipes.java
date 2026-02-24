package nl.melonstudios.create.recipe.server;

import nl.melonstudios.create.recipe.MixingRecipe;
import nl.melonstudios.create.tileentity.TileEntityBasin;

import java.util.HashMap;

public class MixingRecipes {
    public static final MixingRecipes instance = new MixingRecipes();

    public final HashMap<String, MixingRecipe> recipes = new HashMap<>();

    public final void addRecipe(MixingRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public final MixingRecipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public final MixingRecipe getRecipeForInput(TileEntityBasin basin) {
        if (basin == null) return null;
        for (MixingRecipe recipe : this.recipes.values()) {
            if (recipe.matches(basin)) return recipe;
        }
        return null;
    }
}
