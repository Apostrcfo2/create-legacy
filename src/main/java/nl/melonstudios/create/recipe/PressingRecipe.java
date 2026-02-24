package nl.melonstudios.create.recipe;

import com.melonstudios.melonlib.recipe.Ingredient;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PressingRecipe {
    public final Ingredient input;
    public final ItemStack result;

    public PressingRecipe(Ingredient input, ItemStack result) {
        this.input = input;
        this.result = result;
    }
}
