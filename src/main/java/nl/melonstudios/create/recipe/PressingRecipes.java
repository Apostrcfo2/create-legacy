package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashMap;

public class PressingRecipes implements NBTDecodableRecipeType {
    public static final PressingRecipes instance = new PressingRecipes();

    @Override
    public String getRecipeType() {
        return "create:pressing";
    }

    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {

    }

    private PressingRecipes() {

    }

    public final HashMap<String, FlatteningRecipe> recipes = new HashMap<>();

    public final void addRecipe(FlatteningRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    public final void addRecipe(String recipeID, ItemStack input, ItemStack result) {
        this.addRecipe(new FlatteningRecipe(recipeID, input, result));
    }
    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public final FlatteningRecipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public final FlatteningRecipe getRecipeForInput(ItemStack input) {
        for (FlatteningRecipe recipe : this.recipes.values()) {
            if (OreDictionary.itemMatches(recipe.input, input, false)) {
                return recipe;
            }
        }
        return null;
    }
}
