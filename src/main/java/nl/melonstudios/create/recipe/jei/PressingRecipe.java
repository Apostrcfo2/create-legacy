package nl.melonstudios.create.recipe.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class PressingRecipe implements IRecipeWrapper {
    public final ItemStack input;
    public final ItemStack result;

    public PressingRecipe(ItemStack input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInput(VanillaTypes.ITEM, this.input);
        iIngredients.setOutput(VanillaTypes.ITEM, this.result);
    }
}
