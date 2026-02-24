package nl.melonstudios.create.recipe.jei;

import com.melonstudios.melonlib.recipe.Ingredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class JEIPressingRecipe implements IRecipeWrapper {
    public final Ingredient input;
    public final ItemStack result;

    public JEIPressingRecipe(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.input.getDisplayItems()));
        iIngredients.setOutput(VanillaTypes.ITEM, this.result);
    }
}
