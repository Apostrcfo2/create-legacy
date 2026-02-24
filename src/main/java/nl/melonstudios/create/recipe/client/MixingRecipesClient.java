package nl.melonstudios.create.recipe.client;

import com.melonstudios.melonlib.recipe.IRecipeTypeClient;
import nl.melonstudios.create.recipe.MixingRecipe;
import nl.melonstudios.create.recipe.server.MixingRecipes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

public class MixingRecipesClient implements IRecipeTypeClient<MixingRecipe, MixingRecipes> {
    public static final MixingRecipesClient instance = new MixingRecipesClient();

    private MixingRecipesClient() {

    }

    private final Map<String, MixingRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, MixingRecipe> getRecipeMap() {
        return this.recipes;
    }
}
