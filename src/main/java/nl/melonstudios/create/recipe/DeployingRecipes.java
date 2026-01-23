package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.util.Utils;

import java.util.HashMap;

public class DeployingRecipes implements NBTDecodableRecipeType {
    public static final DeployingRecipes instance = new DeployingRecipes();

    @Override
    public String getRecipeType() {
        return "create:deploying";
    }
    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {
        DeployerRecipe recipe = new DeployerRecipe(recipeId, nbt);
        this.addRecipe(recipe);
    }

    private DeployingRecipes() {

    }

    public final HashMap<String, DeployerRecipe> recipes = new HashMap<>();

    public final void addRecipe(DeployerRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    public final void addRecipe(String recipeID, ItemStack input, ItemStack applied, ItemStack result, DeployerRecipe.InputType inputType) {
        this.addRecipe(new DeployerRecipe(recipeID, input, applied, result, inputType));
    }
    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public final DeployerRecipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public final DeployerRecipe getRecipeForInput(ItemStack depotItem, ItemStack deployerItem) {
        for (DeployerRecipe recipe : this.recipes.values()) {
            if (itemMatches(recipe.input, depotItem)) {
                if (itemMatches(recipe.applied, deployerItem)) {
                    return recipe;
                }
            }
        }
        return null;
    }

    private static boolean itemMatches(ItemStack example, ItemStack input) {
        return Utils.itemMatches(example, input);
    }
}
