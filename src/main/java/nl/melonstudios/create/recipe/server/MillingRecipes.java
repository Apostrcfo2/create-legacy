package nl.melonstudios.create.recipe.server;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import com.melonstudios.melonlib.predicates.StackPredicateItem;
import com.melonstudios.melonlib.predicates.StackPredicateMetaItem;
import com.melonstudios.melonlib.predicates.StackPredicateOreDict;
import com.melonstudios.melonlib.recipe.ISyncedRecipeType;
import com.melonstudios.melonlib.recipe.Ingredient;
import com.melonstudios.melonlib.recipe.RecipeException;
import com.melonstudios.melonlib.recipe.UniversalRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import nl.melonstudios.create.recipe.PulverizationRecipe;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MillingRecipes implements ISyncedRecipeType<PulverizationRecipe> {
    public static final MillingRecipes instance = new MillingRecipes();

    private MillingRecipes() {

    }

    public final HashMap<String, PulverizationRecipe> recipes = new HashMap<>();

    public void addRecipe(String recipeID, Ingredient input) {
        throw new IllegalArgumentException("Cannot have recipes with no results!");
    }
    public void addRecipe(String recipeID, Ingredient input, int processingTime) {
        throw new IllegalArgumentException("Cannot have recipes with no results!");
    }
    @SafeVarargs
    public final void addRecipe(String recipeID, Ingredient input, Tuple<ItemStack, Float>... results) {
        this.addRecipe(recipeID, input, 100, results);
    }
    @SafeVarargs
    public final void addRecipe(String recipeID, Ingredient input, int processingTime, Tuple<ItemStack, Float>... results) {
        this.recipes.put(recipeID, new PulverizationRecipe(input, ImmutableList.copyOf(results), processingTime));
    }

    @Override
    public final void removeRecipe(@Nonnull String recipeID) {
        this.recipes.remove(recipeID);
    }

    @Nonnull
    @Override
    public Collection<String> getAllRecipeIDs() {
        return this.recipes.keySet();
    }

    @Nonnull
    @Override
    public Collection<PulverizationRecipe> getAllRecipes() {
        return this.recipes.values();
    }

    @Nonnull
    @Override
    public Map<String, PulverizationRecipe> getRecipeMap() {
        return this.recipes;
    }

    @Nonnull
    @Override
    public PulverizationRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        return null;
    }

    @Override
    public void addRecipe(@Nonnull String recipeID, @Nonnull PulverizationRecipe recipe) {
        this.recipes.put(recipeID, recipe);
    }

    @Override
    public PulverizationRecipe getRecipe(@Nonnull String recipeID) {
        return this.recipes.get(recipeID);
    }

    @Override
    public boolean hasRecipe(@Nonnull String recipeID) {
        return this.recipes.containsKey(recipeID);
    }

    public PulverizationRecipe getRecipeForInput(ItemStack input) {
        for (PulverizationRecipe recipe : this.recipes.values()) {
            if (recipe.input.test(input)) return recipe;
        }
        return null;
    }

    @Override
    public void write(PulverizationRecipe pulverizationRecipe, ByteBuf byteBuf) throws IOException {

    }

    @Override
    public PulverizationRecipe read(String s, ByteBuf byteBuf) throws IOException {
        return null;
    }
}
