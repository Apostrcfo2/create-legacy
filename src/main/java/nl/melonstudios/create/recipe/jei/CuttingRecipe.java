package nl.melonstudios.create.recipe.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class CuttingRecipe implements IRecipeWrapper {
    public final ItemStack input;
    public final List<ItemStack> results;

    public CuttingRecipe(ItemStack input, List<ItemStack> results) {
        this.input = input;
        this.results = results;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, this.input);
        iIngredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(this.results));
    }
}
