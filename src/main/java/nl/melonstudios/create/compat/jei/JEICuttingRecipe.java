package nl.melonstudios.create.compat.jei;

import com.melonstudios.melonlib.recipe.Ingredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public class JEICuttingRecipe implements IRecipeWrapper {
    public final Ingredient input;
    public final List<ItemStack> results;

    public JEICuttingRecipe(Ingredient input, List<ItemStack> results) {
        this.input = input;
        this.results = results;
    }

    @Override
    public void getIngredients(IIngredients iIngredients) {
        iIngredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.input.getDisplayItems()));
        iIngredients.setOutputLists(VanillaTypes.ITEM, Collections.singletonList(this.results));
    }
}
