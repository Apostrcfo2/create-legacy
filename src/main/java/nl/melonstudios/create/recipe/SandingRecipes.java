package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.init.ItemInit;

import java.util.HashMap;
import java.util.Map;

public class SandingRecipes {
    public static final SandingRecipes instance = new SandingRecipes();

    private SandingRecipes() {
        this.addRecipe(MetaItem.of(ItemInit.INGREDIENT, 3).getPredicate(),
                new ItemStack(ItemInit.INGREDIENT, 1, 4));
    }

    private final HashMap<StackPredicate, ItemStack> recipes = new HashMap<>();
    public void addRecipe(StackPredicate input, ItemStack result) {
        this.recipes.put(input, result);
    }
    public ItemStack getResult(ItemStack input) {
        for (Map.Entry<StackPredicate, ItemStack> entry : this.recipes.entrySet()) {
            if (entry.getKey().test(input)) return entry.getValue().copy();
        }
        return ItemStack.EMPTY;
    }
}
