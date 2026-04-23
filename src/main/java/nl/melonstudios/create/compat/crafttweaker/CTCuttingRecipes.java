package nl.melonstudios.create.compat.crafttweaker;

import com.melonstudios.melonlib.recipe.Ingredient;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.CuttingRecipe;
import nl.melonstudios.create.recipe.server.CuttingRecipes;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.create.recipe.CuttingRecipes")
public class CTCuttingRecipes {
    @ZenMethod
    public static void remove(String recipeID) {
        CraftTweakerAPI.apply(new Remove(recipeID));
    }

    @ZenMethod
    public static void remove(IIngredient ingredient) {
        List<String> bin = new ArrayList<>();
        for (IItemStack stack : ingredient.getItems()) {
            ItemStack stk = (ItemStack) stack.getInternal();
            for (Map.Entry<String, CuttingRecipe> entry : CuttingRecipes.instance.recipes.entrySet()) {
                if (entry.getValue().input.matches(stk)) bin.add(entry.getKey());
            }
        }
        for (String recipeID : bin) remove(recipeID);
    }

    @ZenMethod
    public static void removeAll() {
        CraftTweakerAPI.apply(new RemoveAll());
    }

    @ZenMethod
    public static void add(String recipeID, IItemStack input, @Optional boolean inputRespectNBT, IItemStack result, @Optional(valueLong = 300) int recipeTime) {
        CraftTweakerAPI.apply(new Add(recipeID, Ingredient.of((ItemStack) input.getInternal(), inputRespectNBT), (ItemStack) result.getInternal(), recipeTime));
    }

    @ZenMethod
    public static void add(String recipeID, String input, IItemStack result, @Optional(valueLong = 300) int recipeTime) {
        CraftTweakerAPI.apply(new Add(recipeID, Ingredient.of(input), (ItemStack) result.getInternal(), recipeTime));
    }

    private static class Remove implements IAction {
        private final String recipeID;
        private Remove(String recipeID) {
            this.recipeID = recipeID;
        }

        @Override
        public void apply() {
            CuttingRecipes.instance.removeRecipe(this.recipeID);
        }

        @Override
        public String describe() {
            return "Removing Cutting recipe of ID " + this.recipeID;
        }
    }
    private static class RemoveAll implements IAction {
        private RemoveAll() {}

        @Override
        public void apply() {
            CuttingRecipes.instance.recipes.clear();
        }

        @Override
        public String describe() {
            return "Removing all Cutting recipes";
        }
    }
    private static class Add implements IAction {
        private final String recipeID;
        private final Ingredient input;
        private final ItemStack result;
        private final int recipeTime;
        private Add(String recipeID, Ingredient input, ItemStack result, int recipeTime) {
            this.recipeID = recipeID;
            this.input = input;
            this.result = result;
            this.recipeTime = recipeTime;
        }

        @Override
        public void apply() {
            CuttingRecipes.instance.addRecipe(this.recipeID, this.input, this.result, this.recipeTime);
        }

        @Override
        public String describe() {
            return "Adding Cutting recipe of ID " + this.recipeID;
        }
    }
}
