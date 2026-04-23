package nl.melonstudios.create.compat.crafttweaker;

import com.melonstudios.melonlib.recipe.Ingredient;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.PressingRecipe;
import nl.melonstudios.create.recipe.server.PressingRecipes;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.create.recipe.PressingRecipes")
public class CTPressingRecipes {
    @ZenMethod
    public static void remove(String recipeID) {
        CraftTweakerAPI.apply(new Remove(recipeID));
    }

    @ZenMethod
    public static void remove(IIngredient ingredient) {
        List<String> bin = new ArrayList<>();
        for (IItemStack stack : ingredient.getItems()) {
            ItemStack stk = (ItemStack) stack.getInternal();
            for (Map.Entry<String, PressingRecipe> entry : PressingRecipes.instance.recipes.entrySet()) {
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
    public static void add(String recipeID, IIngredient input, IItemStack result) {
        List<IItemStack> results = result.getItems();
        if (!results.isEmpty()) {
            if (results.size() > 1) return;
            List<IItemStack> inputs = input.getItems();
            if (!inputs.isEmpty()) {
                if (inputs.size() > 1) {
                    for (int i = 0; i < inputs.size(); i++) {
                        CraftTweakerAPI.apply(new Add(recipeID + i,
                                Ingredient.of((ItemStack) inputs.get(i).getInternal(), false),
                                (ItemStack) results.get(0).getInternal()));
                    }
                } else {
                    CraftTweakerAPI.apply(new Add(recipeID,
                            Ingredient.of((ItemStack) inputs.get(0).getInternal(), false),
                            (ItemStack) results.get(0).getInternal()));
                }
            }
        }
    }

    @ZenMethod
    public static void add(String recipeID, String oredict, IItemStack result) {
        List<IItemStack> results = result.getItems();
        if (!results.isEmpty()) {
            if (results.size() == 1) {
                CraftTweakerAPI.apply(new Add(recipeID, Ingredient.of(oredict), (ItemStack) results.get(0).getInternal()));
            }
        }
    }

    private static class Remove implements IAction {
        private final String recipeID;
        private Remove(String recipeID) {
            this.recipeID = recipeID;
        }

        @Override
        public void apply() {
            PressingRecipes.instance.removeRecipe(this.recipeID);
        }

        @Override
        public String describe() {
            return "Removing Pressing recipe of ID " + this.recipeID;
        }
    }
    private static class RemoveAll implements IAction {
        private RemoveAll() {}

        @Override
        public void apply() {
            PressingRecipes.instance.recipes.clear();
        }

        @Override
        public String describe() {
            return "Removing all Pressing recipes";
        }
    }
    private static class Add implements IAction {
        private final String recipeID;
        private final Ingredient input;
        private final ItemStack result;
        private Add(String recipeID, Ingredient input, ItemStack result) {
            this.recipeID = recipeID;
            this.input = input;
            this.result = result;
        }

        @Override
        public void apply() {
            PressingRecipes.instance.addRecipe(this.recipeID, this.input, this.result);
        }

        @Override
        public String describe() {
            return "Adding Pressing recipe of ID " + this.recipeID;
        }
    }
}
