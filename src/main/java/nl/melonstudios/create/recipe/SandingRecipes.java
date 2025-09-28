package nl.melonstudios.create.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.init.ItemInit;

import java.util.HashMap;
import java.util.Map;

public class SandingRecipes implements NBTDecodableRecipeType {
    public static final SandingRecipes instance = new SandingRecipes();

    @Override
    public String getRecipeType() {
        return "create:sanding";
    }
    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {
        this.addRecipe(new ItemStack(nbt.getCompoundTag("Input")), new ItemStack(nbt.getCompoundTag("Result")));
    }

    private SandingRecipes() {
        this.addRecipe(new ItemStack(ItemInit.INGREDIENT, 1, 3),
                new ItemStack(ItemInit.INGREDIENT, 1, 4));
    }

    public final HashMap<ItemStack, ItemStack> recipes = new HashMap<>();
    public void addRecipe(ItemStack input, ItemStack result) {
        this.recipes.put(input, result);
    }
    public ItemStack getResult(ItemStack input) {
        for (Map.Entry<ItemStack, ItemStack> entry : this.recipes.entrySet()) {
            if (entry.getKey().isItemEqual(input)) return entry.getValue().copy();
        }
        return ItemStack.EMPTY;
    }
}
