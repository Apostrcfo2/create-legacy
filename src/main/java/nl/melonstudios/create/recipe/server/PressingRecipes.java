package nl.melonstudios.create.recipe.server;

import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.recipe.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.recipe.PressingRecipe;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PressingRecipes implements ISyncedRecipeType<PressingRecipe> {
    public static final PressingRecipes instance = new PressingRecipes();

    private PressingRecipes() {

    }

    public final HashMap<String, PressingRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, PressingRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Nonnull
    @Override
    public PressingRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        try {
            return new PressingRecipe(universal.itemInputs.get(0).get(0), universal.itemOutputs.get(0).get(0));
        } catch (Throwable e) {
            throw new RecipeException(e);
        }
    }

    public final void addRecipe(String recipeID, Ingredient input, ItemStack result) {
        this.addRecipe(recipeID, new PressingRecipe(input, result));
    }

    public static PressingRecipe getRecipeForInput(ItemStack input, boolean client) {
        IRecipeAccessor<PressingRecipe> accessor = RecipeInit.getPressingRecipes(client);

        for (String recipeID : accessor.getAllRecipeIDs()) {
            PressingRecipe recipe = accessor.getRecipe(recipeID);
            if (recipe == null) continue;
            if (recipe.input.matches(input)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public void write(PressingRecipe recipe, ByteBuf buf) throws IOException {
        recipe.input.serialize(buf);
        StackUtil.writeItemStack(recipe.result, buf, true, true);
    }

    @Override
    public PressingRecipe read(String recipeID, ByteBuf buf) throws IOException {
        Ingredient input = Ingredient.read(buf);
        ItemStack result = StackUtil.readItemStack(buf, true, true);
        return new PressingRecipe(input, result);
    }
}
