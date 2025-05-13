package nl.melonstudios.create.recipe.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.SandingRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeMaker {
    public static List<SandingRecipe> getSandingRecipes(IJeiHelpers helpers) {
        IStackHelper stackHelper = helpers.getStackHelper();
        SandingRecipes instance = SandingRecipes.instance;

        Map<ItemStack, ItemStack> recipeMap = instance.recipes;
        List<SandingRecipe> recipeList = new ArrayList<>(recipeMap.size());

        for (Map.Entry<ItemStack, ItemStack> entry : recipeMap.entrySet()) {
            recipeList.add(new SandingRecipe(entry.getKey(), entry.getValue()));
        }

        return recipeList;
    }
}
