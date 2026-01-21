package nl.melonstudios.create.recipe.jei;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.recipe.IStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.recipe.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RecipeMaker {
    public static List<PressingRecipe> getPressingRecipes(IJeiHelpers helpers) {
        PressingRecipes instance = PressingRecipes.instance;

        List<FlatteningRecipe> recipes = new ArrayList<>(instance.recipes.values());
        List<PressingRecipe> recipeList = new ArrayList<>(recipes.size());

        for (FlatteningRecipe recipe : recipes) {
            recipeList.add(new PressingRecipe(recipe.input, recipe.result));
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

    public static List<CuttingRecipe> getCuttingRecipes(IJeiHelpers helpers) {
        CuttingRecipes instance = CuttingRecipes.instance;

        List<SawingRecipe> recipes = new ArrayList<>(instance.recipes.values());
        List<CuttingRecipe> recipeList = new ArrayList<>();

        List<SawingRecipe> bin = new ArrayList<>();
        while (!recipes.isEmpty()) {
            bin.clear();
            SawingRecipe example = recipes.remove(0);
            ItemStack next = example.input;
            List<ItemStack> out = new ArrayList<>();
            out.add(example.result.copy());
            for (SawingRecipe recipe : recipes) {
                if (OreDictionary.itemMatches(next, recipe.input, false)) {
                    bin.add(recipe);
                    out.add(recipe.result.copy());
                }
            }
            recipes.removeAll(bin);
            recipeList.add(new CuttingRecipe(next, out));
        }

        return recipeList;
    }

    public static List<JEIDeployerRecipe> getDeployingRecipes(IJeiHelpers helpers) {
        DeployingRecipes instance = DeployingRecipes.instance;

        Collection<DeployerRecipe> recipes = instance.recipes.values();
        List<JEIDeployerRecipe> recipeList = new ArrayList<>();

        for (DeployerRecipe recipe : recipes) {
            recipeList.add(new JEIDeployerRecipe(recipe.input, recipe.applied, recipe.result));
        }

        return recipeList;
    }
}
