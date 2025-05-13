package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import nl.melonstudios.create.init.ItemInit;

import java.util.HashMap;
import java.util.Map;

public class SandingRecipes {
    public static final SandingRecipes instance = new SandingRecipes();

    private SandingRecipes() {
        this.addRecipe(new ItemStack(ItemInit.INGREDIENT, 3),
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
