package nl.melonstudios.create.recipe.client;

import com.melonstudios.melonlib.recipe.IRecipeTypeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.recipe.sequence.SequenceRecipe;
import nl.melonstudios.create.recipe.sequence.SequencedRecipes;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class SequencedRecipesClient implements IRecipeTypeClient<SequenceRecipe, SequencedRecipes> {
    public static final SequencedRecipesClient instance = new SequencedRecipesClient();

    private SequencedRecipesClient() {

    }

    private final Map<String, SequenceRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, SequenceRecipe> getRecipeMap() {
        return this.recipes;
    }
}
