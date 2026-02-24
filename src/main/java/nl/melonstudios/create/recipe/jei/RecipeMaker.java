package nl.melonstudios.create.recipe.jei;

import com.melonstudios.melonlib.recipe.Ingredient;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.*;
import nl.melonstudios.create.recipe.client.CuttingRecipesClient;
import nl.melonstudios.create.recipe.server.PressingRecipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecipeMaker {
    public static List<JEIPressingRecipe> getPressingRecipes(IJeiHelpers helpers) {
        PressingRecipes instance = PressingRecipes.instance;

        List<PressingRecipe> recipes = new ArrayList<>(instance.recipes.values());
        List<JEIPressingRecipe> recipeList = new ArrayList<>(recipes.size());

        for (PressingRecipe recipe : recipes) {
            recipeList.add(new JEIPressingRecipe(recipe.input, recipe.result));
        }

        return recipeList;
    }

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

    public static List<JEICuttingRecipe> getCuttingRecipes(IJeiHelpers helpers) {
        CuttingRecipesClient instance = CuttingRecipesClient.instance;

        List<CuttingRecipe> recipes = new ArrayList<>(instance.getAllRecipes());
        List<JEICuttingRecipe> recipeList = new ArrayList<>();

        List<CuttingRecipe> bin = new ArrayList<>();
        while (!recipes.isEmpty()) {
            bin.clear();
            CuttingRecipe example = recipes.remove(0);
            Ingredient next = example.input;
            List<ItemStack> out = new ArrayList<>();
            out.add(example.result.copy());
            for (CuttingRecipe recipe : recipes) {
                if (recipe.input.equals(next)) {
                    bin.add(recipe);
                    out.add(recipe.result.copy());
                }
            }
            recipes.removeAll(bin);
            recipeList.add(new JEICuttingRecipe(next, out));
        }

        return recipeList;
    }
}
