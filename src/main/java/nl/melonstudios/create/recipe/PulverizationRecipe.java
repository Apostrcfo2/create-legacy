package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.recipe.Ingredient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;

import java.util.List;

public final class PulverizationRecipe {
    public final Ingredient input;
    public final List<Tuple<ItemStack, Float>> results;
    public final int processingTime;

    public PulverizationRecipe(Ingredient input, List<Tuple<ItemStack, Float>> results, int processingTime) {
        this.input = input;
        this.results = results;
        this.processingTime = processingTime;
    }
}
