package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.util.filter.IItemFilter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CuttingRecipes implements NBTDecodableRecipeType {
    public static final CuttingRecipes instance = new CuttingRecipes();

    @Override
    public String getRecipeType() {
        return "create:cutting";
    }
    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {
        SawingRecipe recipe = new SawingRecipe(recipeId, nbt);
        this.addRecipe(recipe);
    }

    private CuttingRecipes() {

    }

    public final HashMap<String, SawingRecipe> recipes = new HashMap<>();

    public final void addRecipe(SawingRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    public final void addRecipe(String recipeID, ItemStack input, ItemStack result, int processingTime) {
        this.addRecipe(new SawingRecipe(recipeID, input, result, processingTime));
    }
    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public final SawingRecipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public final SawingRecipe getRecipeForInput(ItemStack input, @Nullable IItemFilter recipeFilter, int rotation) {
        List<SawingRecipe> candidates = new ArrayList<>();
        for (SawingRecipe recipe : this.recipes.values()) {
            if (OreDictionary.itemMatches(recipe.input, input, false)) {
                if (recipeFilter == null || recipeFilter.matches(recipe.result)) {
                    candidates.add(recipe);
                }
            }
        }
        return candidates.get((int) (Integer.toUnsignedLong(rotation) % candidates.size()));
    }
}
