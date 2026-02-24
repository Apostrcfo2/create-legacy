package nl.melonstudios.create.recipe.server;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import com.melonstudios.melonlib.predicates.StackPredicateItem;
import com.melonstudios.melonlib.predicates.StackPredicateMetaItem;
import com.melonstudios.melonlib.predicates.StackPredicateOreDict;
import com.melonstudios.melonlib.recipe.ISyncedRecipeType;
import com.melonstudios.melonlib.recipe.RecipeException;
import com.melonstudios.melonlib.recipe.UniversalRecipe;
import io.netty.buffer.ByteBuf;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Tuple;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.recipe.PulverizationRecipe;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MillingRecipes implements ISyncedRecipeType<PulverizationRecipe> {
    public static final MillingRecipes instance = new MillingRecipes();

    private MillingRecipes() {
        this.addRecipe("create:allium", new StackPredicateMetaItem(MetaItem.of(Item.getItemFromBlock(Blocks.RED_FLOWER), 2)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 13), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 2, 5), 0.1F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 9), 0.1F)
        );
        this.addRecipe("create:andesite", new StackPredicateMetaItem(MetaItem.of(Blocks.STONE, 5)),
                new Tuple<>(new ItemStack(Blocks.COBBLESTONE), 1.0F)
        );
        this.addRecipe("create:azure_bluet", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 3)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 0.1F)
        );
        this.addRecipe("create:beetroot", new StackPredicateItem(Items.BEETROOT),
                new Tuple<>(new ItemStack(Items.DYE, 2, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.BEETROOT_SEEDS), 0.1F)
        );
        this.addRecipe("create:blue_orchid", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 1)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 12), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 0.05F)
        );
        this.addRecipe("create:bone", new StackPredicateItem(Items.BONE),
                new Tuple<>(new ItemStack(Items.DYE, 3, 15), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 7), 0.25F),
                new Tuple<>(new ItemStack(Items.DYE, 3, 15), 0.25F)
        );
        this.addRecipe("create:bone_meal", new StackPredicateMetaItem(MetaItem.of(Items.DYE, 15)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 7), 0.1F)
        );
        this.addRecipe("create:cactus", new StackPredicateItem(Item.getItemFromBlock(Blocks.CACTUS)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 2), 0.1F)
        );
        this.addRecipe("create:clay", new StackPredicateItem(Item.getItemFromBlock(Blocks.CLAY)),
                new Tuple<>(new ItemStack(Items.CLAY_BALL, 3), 1.0F),
                new Tuple<>(new ItemStack(Items.CLAY_BALL), 0.5F)
        );
        this.addRecipe("create:coal", new StackPredicateItem(Items.COAL),
                new Tuple<>(new ItemStack(Items.DYE, 1, 8), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 2, 8), 1.0F)
        );
        this.addRecipe("create:cobblestone", new StackPredicateItem(Item.getItemFromBlock(Blocks.COBBLESTONE)),
                new Tuple<>(new ItemStack(Blocks.GRAVEL), 1.0F)
        );
        this.addRecipe("create:dandelion", new StackPredicateMetaItem(MetaItem.of(Blocks.YELLOW_FLOWER, 0)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 11), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 11), 0.05F)
        );
        this.addRecipe("create:fern", new StackPredicateMetaItem(MetaItem.of(Blocks.TALLGRASS, 2)),
                new Tuple<>(new ItemStack(Items.DYE, 1, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.WHEAT_SEEDS), 0.1F)
        );
        this.addRecipe("create:granite", new StackPredicateOreDict("stoneGranite"),
                new Tuple<>(new ItemStack(Blocks.SAND, 1, 1), 1.0F)
        );
        this.addRecipe("create:grass", new StackPredicateMetaItem(MetaItem.of(Blocks.TALLGRASS, 1)),
                new Tuple<>(new ItemStack(Items.WHEAT_SEEDS),  0.25F)
        );
        this.addRecipe("create:gravel", new StackPredicateItem(Item.getItemFromBlock(Blocks.GRAVEL)),
                new Tuple<>(new ItemStack(Items.FLINT), 1.0F)
        );
        this.addRecipe("create:ink_sac", new StackPredicateMetaItem(MetaItem.of(Items.DYE, 0)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 8), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 8), 0.1F)
        );
        this.addRecipe("create:large_fern", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 3)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 2), 0.5F),
                new Tuple<>(new ItemStack(Items.WHEAT_SEEDS), 0.1F)
        );
        this.addRecipe("create:lilac", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 1)),
                new Tuple<>(new ItemStack(Items.DYE, 3, 13), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 13), 0.25F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 5), 0.25F)
        );
        this.addRecipe("create:orange_tulip", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 5)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 14), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 10), 0.1F)
        );
        this.addRecipe("create:oxeye_daisy", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 8)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 7), 0.2F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 11), 0.05F)
        );
        this.addRecipe("create:peony", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 5)),
                new Tuple<>(new ItemStack(Items.DYE,3, 9), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE,3, 13), 0.25F),
                new Tuple<>(new ItemStack(Items.DYE,1, 9), 0.25F)
        );
        this.addRecipe("create:pink_tulip", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 7)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 9), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 10), 0.1F)
        );
        this.addRecipe("create:poppy", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 0)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 1), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 2), 0.05F)
        );
        this.addRecipe("create:red_tulip", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 4)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 1), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 10), 0.1F)
        );
        this.addRecipe("create:rose_bush", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 4)),
                new Tuple<>(new ItemStack(Items.DYE, 3, 1), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 2), 0.05F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 1), 0.25F)
        );
        this.addRecipe("create:saddle", new StackPredicateItem(Items.SADDLE),
                new Tuple<>(new ItemStack(Items.LEATHER, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.LEATHER, 2), 0.5F)
        );
        this.addRecipe("create:sandstone", new StackPredicateMetaItem(MetaItem.of(Blocks.SANDSTONE, 0)),
                new Tuple<>(new ItemStack(Blocks.SAND, 1, 0), 1.0F)
        );
        this.addRecipe("create:sugar_canes", new StackPredicateItem(Items.REEDS),
                new Tuple<>(new ItemStack(Items.SUGAR, 2), 1.0F),
                new Tuple<>(new ItemStack(Items.SUGAR, 1), 0.1F)
        );
        this.addRecipe("create:sunflower", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 0)),
                new Tuple<>(new ItemStack(Items.DYE, 3, 11), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 14), 0.25F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 11), 0.25F)
        );
        this.addRecipe("create:double_tallgrass", new StackPredicateMetaItem(MetaItem.of(Blocks.DOUBLE_PLANT, 2)),
                new Tuple<>(new ItemStack(Items.WHEAT_SEEDS), 0.5F)
        );
        this.addRecipe("create:terracotta", new StackPredicateItem(Item.getItemFromBlock(Blocks.HARDENED_CLAY)),
                new Tuple<>(new ItemStack(Blocks.SAND, 1, 1), 1.0F)
        );
        this.addRecipe("create:wheat", new StackPredicateItem(Items.WHEAT),
                new Tuple<>(new ItemStack(ItemInit.INGREDIENT, 1, 0), 1.0F),
                new Tuple<>(new ItemStack(ItemInit.INGREDIENT, 2, 0), 0.25F),
                new Tuple<>(new ItemStack(Items.WHEAT_SEEDS), 0.25F)
        );
        this.addRecipe("create:white_tulip", new StackPredicateMetaItem(MetaItem.of(Blocks.RED_FLOWER, 1)),
                new Tuple<>(new ItemStack(Items.DYE, 2, 7), 1.0F),
                new Tuple<>(new ItemStack(Items.DYE, 1, 10), 0.1F)
        );
        this.addRecipe("create:wool", new StackPredicateOreDict("wool"),
                new Tuple<>(new ItemStack(Items.STRING), 1.0F)
        );
    }

    public final HashMap<String, PulverizationRecipe> recipes = new HashMap<>();

    public final void addRecipe(PulverizationRecipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    @SuppressWarnings("unused")
    public final void addRecipe(String recipeID, StackPredicate input) {
        throw new IllegalArgumentException("Cannot have recipe with no results! Did you want removeRecipe() instead?");
    }
    @SafeVarargs
    public final void addRecipe(String recipeID, StackPredicate input, int processingTime, Tuple<ItemStack, Float>... results) {
        this.addRecipe(new PulverizationRecipe(recipeID, input, ImmutableList.copyOf(results), processingTime));
    }
    @SafeVarargs
    public final void addRecipe(String recipeID, StackPredicate input, Tuple<ItemStack, Float>... results) {
        this.addRecipe(new PulverizationRecipe(recipeID, input, ImmutableList.copyOf(results), 100));
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
