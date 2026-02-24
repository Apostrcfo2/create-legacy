package nl.melonstudios.create.recipe.sequence;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.recipe.*;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.init.RecipeInit;
import nl.melonstudios.create.recipe.NBTDecodableRecipeType;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.*;

public class SequencedRecipes implements ISyncedRecipeType<SequenceRecipe> {
    public static final SequencedRecipes instance = new SequencedRecipes();

    private SequencedRecipes() {

    }

    public final HashMap<String, SequenceRecipe> recipes = new HashMap<>();

    @Nonnull
    @Override
    public Map<String, SequenceRecipe> getRecipeMap() {
        return this.recipes;
    }

    public void addRecipe(String recipeID, Ingredient input, ItemStack processing, List<SequenceStep> steps, int repetitions, SequenceResult result) {
        this.addRecipe(recipeID, new SequenceRecipe(input, processing, steps, repetitions, result));
    }
    public void addRecipe(String recipeID, Ingredient input, ItemStack processing, List<SequenceStep> steps, int repetitions, ItemStack result) {
        this.addRecipe(recipeID, input, processing, steps, repetitions, new SequenceResult(result));
    }

    @Nonnull
    @Override
    public SequenceRecipe convert(@Nonnull UniversalRecipe universal) throws RecipeException {
        throw new RecipeException("Sequenced recipes do not support universal recipes");
    }

    public static String getRecipeForInput(ItemStack stack) {
        IRecipeAccessor<SequenceRecipe> accessor = RecipeInit.getSequenceRecipes();
        for (String recipeID : accessor.getAllRecipeIDs()) {
            SequenceRecipe recipe = accessor.getRecipe(recipeID);
            if (recipe == null) continue;
            if (recipe.input.matches(stack)) return recipeID;
        }
        return null;
    }

    @Override
    public void write(SequenceRecipe recipe, ByteBuf buf) throws IOException {
        recipe.input.serialize(buf);
        StackUtil.writeItemStack(recipe.processing, buf, false, false);
        buf.writeInt(recipe.steps.size());
        for (SequenceStep step : recipe.steps) {
            step.write(buf);
        }
        buf.writeInt(recipe.repetitions);
        recipe.result.write(buf);
    }

    @Override
    public SequenceRecipe read(String recipeID, ByteBuf buf) throws IOException {
        Ingredient input = Ingredient.read(buf);
        ItemStack processing = StackUtil.readItemStack(buf, false, false);
        int stepsSize = buf.readInt();
        List<SequenceStep> steps = new ArrayList<>(stepsSize);
        for (int i = 0; i < stepsSize; i++) {
            steps.add(SequenceStep.read(buf));
        }
        int repetitions = buf.readInt();
        SequenceResult result = SequenceResult.read(buf);
        return new SequenceRecipe(input, processing, steps, repetitions, result);
    }
}
