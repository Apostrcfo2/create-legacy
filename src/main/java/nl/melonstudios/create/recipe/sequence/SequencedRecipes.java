package nl.melonstudios.create.recipe.sequence;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.recipe.NBTDecodableRecipeType;
import nl.melonstudios.create.util.Utils;

import java.util.HashMap;
import java.util.List;

public class SequencedRecipes implements NBTDecodableRecipeType {
    public static final SequencedRecipes instance = new SequencedRecipes();

    @Override
    public String getRecipeType() {
        return "create:sequenced_assembly";
    }

    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {
        SequenceRecipe recipe = new SequenceRecipe(recipeId, nbt);
    }

    private SequencedRecipes() {

    }

    public final HashMap<String, SequenceRecipe> recipes = new HashMap<>();

    public final void addRecipe(SequenceRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    public final void addRecipe(String recipeID, ItemStack input, ItemStack processing, List<SequenceStep> steps, int repetitions, SequenceResult result) {
        this.addRecipe(new SequenceRecipe(recipeID, input, processing, steps, repetitions, result));
    }
    public final void addRecipe(String recipeID, ItemStack input, ItemStack processing, List<SequenceStep> steps, int repetitions, ItemStack result) {
        this.addRecipe(recipeID, input, processing, steps, repetitions, new SequenceResult(result));
    }
    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public final SequenceRecipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public final SequenceRecipe getRecipe(ItemStack stack) {
        for (SequenceRecipe recipe : this.recipes.values()) {
            if (Utils.itemMatches(recipe.input, stack)) {
                return recipe;
            }
        }
        return null;
    }
}
