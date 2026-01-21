package nl.melonstudios.create.recipe.jei;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

public class JEIDeployerRecipe implements IRecipeWrapper {
    public final ItemStack input;
    public final ItemStack applied;
    public final ItemStack result;

    public JEIDeployerRecipe(ItemStack input, ItemStack applied, ItemStack result) {
        this.input = input;
        this.applied = applied;
        this.result = result;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInput(VanillaTypes.ITEM, this.input);
        ingredients.setInput(VanillaTypes.ITEM, this.applied);
        ingredients.setOutput(VanillaTypes.ITEM, this.result);
    }
}
