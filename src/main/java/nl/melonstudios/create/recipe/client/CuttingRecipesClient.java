package nl.melonstudios.create.recipe.client;

import com.melonstudios.melonlib.recipe.IRecipeTypeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.recipe.CuttingRecipe;
import nl.melonstudios.create.recipe.server.CuttingRecipes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class CuttingRecipesClient implements IRecipeTypeClient<CuttingRecipe, CuttingRecipes> {
    public static final CuttingRecipesClient instance = new CuttingRecipesClient();

    private CuttingRecipesClient() {

    }

    private final Map<String, CuttingRecipe> recipes = new HashMap<>();

    @Nullable
    @Override
    public CuttingRecipe getRecipe(@Nonnull String recipeID) {
        return this.recipes.get(recipeID);
    }

    @Override
    public boolean hasRecipe(@Nonnull String recipeID) {
        return this.recipes.containsKey(recipeID);
    }

    @Nonnull
    @Override
    public Collection<String> getAllRecipeIDs() {
        return this.recipes.keySet();
    }

    @Nonnull
    @Override
    public Collection<CuttingRecipe> getAllRecipes() {
        return this.recipes.values();
    }

    @Nonnull
    @Override
    public Map<String, CuttingRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Override
    public void prepareForData() {
        this.recipes.clear();
    }

    @Override
    public void addFromLocal(@Nonnull CuttingRecipes local) {
        this.recipes.putAll(local.recipes);
    }

    @Override
    public void addFromRemote(@Nonnull String recipeID, @Nonnull CuttingRecipe recipe) {
        this.recipes.put(recipeID, recipe);
    }
}
