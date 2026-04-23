package nl.melonstudios.create.compat.crafttweaker;

import com.melonstudios.melonlib.recipe.Ingredient;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.IAction;
import crafttweaker.annotations.ZenRegister;
import crafttweaker.api.item.IIngredient;
import crafttweaker.api.item.IItemStack;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.recipe.DeployerRecipe;
import nl.melonstudios.create.recipe.server.DeployerRecipes;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ZenRegister
@ZenClass("mods.create.recipe.DeployerRecipes")
public class CTDeployerRecipes {
    @ZenMethod
    public static void remove(String recipeID) {
        CraftTweakerAPI.apply(new Remove(recipeID));
    }

    @ZenMethod
    public static void remove(IIngredient ingredient) {
        List<String> bin = new ArrayList<>();
        for (IItemStack stack : ingredient.getItems()) {
            ItemStack stk = (ItemStack) stack.getInternal();
            for (Map.Entry<String, DeployerRecipe> entry : DeployerRecipes.instance.recipes.entrySet()) {
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
    public static void add(String recipeID,
                           IItemStack input, @Optional boolean inputRespectNBT,
                           IItemStack applied, @Optional boolean appliedRespectNBT,
                           IItemStack result, @Optional String inputType
    ) {
        CraftTweakerAPI.apply(new Add(recipeID,
                Ingredient.of((ItemStack) input.getInternal(), inputRespectNBT),
                Ingredient.of((ItemStack) applied.getInternal(), appliedRespectNBT),
                (ItemStack) result.getInternal(), DeployerRecipe.InputType.get(inputType)
        ));
    }

    @ZenMethod
    public static void add(String recipeID,
                           String input,
                           IItemStack applied, @Optional boolean appliedRespectNBT,
                           IItemStack result, @Optional String inputType
    ) {
        CraftTweakerAPI.apply(new Add(recipeID,
                Ingredient.of(input),
                Ingredient.of((ItemStack) applied.getInternal(), appliedRespectNBT),
                (ItemStack) result.getInternal(), DeployerRecipe.InputType.get(inputType)
        ));
    }

    @ZenMethod
    public static void add(String recipeID,
                           IItemStack input, @Optional boolean inputRespectNBT,
                           String applied,
                           IItemStack result, @Optional String inputType
    ) {
        CraftTweakerAPI.apply(new Add(recipeID,
                Ingredient.of((ItemStack) input.getInternal(), inputRespectNBT),
                Ingredient.of(applied),
                (ItemStack) result.getInternal(), DeployerRecipe.InputType.get(inputType)
        ));
    }

    @ZenMethod
    public static void add(String recipeID,
                           String input,
                           String applied,
                           IItemStack result, @Optional String inputType
    ) {
        CraftTweakerAPI.apply(new Add(recipeID,
                Ingredient.of(input),
                Ingredient.of(applied),
                (ItemStack) result.getInternal(), DeployerRecipe.InputType.get(input)
        ));
    }

    private static class Remove implements IAction {
        private final String recipeID;
        private Remove(String recipeID) {
            this.recipeID = recipeID;
        }

        @Override
        public void apply() {
            DeployerRecipes.instance.removeRecipe(this.recipeID);
        }

        @Override
        public String describe() {
            return "Removing Deployer recipe of ID " + this.recipeID;
        }
    }
    private static class RemoveAll implements IAction {
        private RemoveAll() {}

        @Override
        public void apply() {
            DeployerRecipes.instance.recipes.clear();
        }

        @Override
        public String describe() {
            return "Removing all Deployer recipes";
        }
    }
    private static class Add implements IAction {
        private final String recipeID;
        private final Ingredient input;
        private final Ingredient applied;
        private final ItemStack result;
        private final DeployerRecipe.InputType inputType;
        private Add(String recipeID, Ingredient input, Ingredient applied, ItemStack result, DeployerRecipe.InputType inputType) {
            this.recipeID = recipeID;
            this.input = input;
            this.applied = applied;
            this.result = result;
            this.inputType = inputType;
        }

        @Override
        public void apply() {
            DeployerRecipes.instance.addRecipe(this.recipeID, this.input, this.applied, this.result, this.inputType);
        }

        @Override
        public String describe() {
            return "Adding Deployer recipe of ID " + this.recipeID;
        }
    }
}
