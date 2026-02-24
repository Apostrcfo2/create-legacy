package nl.melonstudios.create.recipe.server;

import com.melonstudios.melonlib.recipe.IRecipeAccessor;
import com.melonstudios.melonlib.recipe.ISyncedRecipeType;
import com.melonstudios.melonlib.recipe.RecipeException;
import com.melonstudios.melonlib.recipe.UniversalRecipe;
import io.netty.buffer.ByteBuf;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.recipe.MixingRecipe;
import nl.melonstudios.create.tileentity.TileEntityBasin;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MixingRecipes implements ISyncedRecipeType<MixingRecipe> {
    public static final MixingRecipes instance = new MixingRecipes();

    private MixingRecipes() {

    }

    public final HashMap<String, MixingRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, MixingRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Nonnull
    @Override
    public MixingRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        throw new RecipeException("unsupported");
    }

    public static String getRecipeForInput(TileEntityBasin basin) {
        if (basin == null) return null;
        IRecipeAccessor<MixingRecipe> accessor = RecipeInit.getMixingRecipes();
        for (String recipeID : accessor.getAllRecipeIDs()) {
            MixingRecipe recipe = accessor.getRecipe(recipeID);
            if (recipe == null) continue;
            if (recipe.matches(basin)) return recipeID;
        }
        return null;
    }

    @Override
    public void write(MixingRecipe recipe, ByteBuf buf) throws IOException {
        recipe.write(buf);
    }

    @Override
    public MixingRecipe read(String recipeID, ByteBuf buf) throws IOException {
        return MixingRecipe.read(buf);
    }
}
