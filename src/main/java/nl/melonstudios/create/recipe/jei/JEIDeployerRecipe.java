package nl.melonstudios.create.recipe.jei;

import com.melonstudios.melonlib.recipe.Ingredient;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

import java.util.Collections;

public class JEIDeployerRecipe implements IRecipeWrapper {
    public final Ingredient input;
    public final Ingredient applied;
    public final ItemStack result;

    public JEIDeployerRecipe(Ingredient input, Ingredient applied, ItemStack result) {
        this.input = input;
        this.applied = applied;
        this.result = result;
    }

    @Override
    public void getIngredients(IIngredients ingredients) {
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.input.getDisplayItems()));
        ingredients.setInputLists(VanillaTypes.ITEM, Collections.singletonList(this.applied.getDisplayItems()));
        ingredients.setOutput(VanillaTypes.ITEM, this.result);
    }
}
