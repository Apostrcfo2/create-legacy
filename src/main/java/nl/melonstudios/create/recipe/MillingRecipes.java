package nl.melonstudios.create.recipe;

import com.google.common.collect.ImmutableList;
import com.melonstudios.melonlib.misc.MetaItem;
import com.melonstudios.melonlib.predicates.StackPredicate;
import com.melonstudios.melonlib.predicates.StackPredicateItem;
import com.melonstudios.melonlib.predicates.StackPredicateMetaItem;
import com.melonstudios.melonlib.predicates.StackPredicateOreDict;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MillingRecipes implements NBTDecodableRecipeType {
    public static final MillingRecipes instance = new MillingRecipes();

    @Override
    public String getRecipeType() {
        return "create:milling";
    }
    @Override
    public void decodeRecipe(String recipeId, NBTTagCompound nbt) {
        Recipe recipe = new Recipe(recipeId, nbt);
        this.addRecipe(recipe);
    }

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
        this.addRecipe("create:terracotta", new StackPredicateItem(Item.getItemFromBlock(Blocks.HARDENED_CLAY)),
                new Tuple<>(new ItemStack(Blocks.SAND, 1, 1), 1.0F)
        );
        this.addRecipe("create:wool", new StackPredicateOreDict("wool"),
                new Tuple<>(new ItemStack(Items.STRING), 1.0F)
        );
        //TODO: the last few recipes (see the create wiki for reference)
    }

    public final HashMap<String, Recipe> recipes = new HashMap<>();

    public final void addRecipe(Recipe recipe) {
        this.recipes.put(recipe.recipeID, recipe);
    }
    @SuppressWarnings("unused")
    public final void addRecipe(String recipeID, StackPredicate input) {
        throw new IllegalArgumentException("Cannot have recipe with no results! Did you want removeRecipe() instead?");
    }
    @SafeVarargs
    public final void addRecipe(String recipeID, StackPredicate input, Tuple<ItemStack, Float>... results) {
        this.addRecipe(new Recipe(recipeID, input, ImmutableList.copyOf(results)));
    }

    public final void removeRecipe(String recipeID) {
        this.recipes.remove(recipeID);
    }

    public Recipe getRecipe(String recipeID) {
        return this.recipes.get(recipeID);
    }
    public Recipe getRecipeForInput(ItemStack input) {
        for (Recipe recipe : this.recipes.values()) {
            if (recipe.input.test(input)) return recipe;
        }
        return null;
    }

    public static final class Recipe {
        public final String recipeID;
        public final StackPredicate input;
        public final List<Tuple<ItemStack, Float>> results;

        public Recipe(String recipeID, StackPredicate input, List<Tuple<ItemStack, Float>> results) {
            this.recipeID = recipeID;
            this.input = input;
            this.results = results;
        }

        private Recipe(String recipeID, NBTTagCompound nbt) {
            this.recipeID = recipeID;
            this.input = new StackPredicateMetaItem(
                    MetaItem.of(ForgeRegistries.ITEMS.getValue(
                            new ResourceLocation(nbt.getString("inputID"))),
                            nbt.getInteger("inputMeta")
                    )
            );
            NBTTagList resultList = nbt.getTagList("Results", 10);
            ArrayList<Tuple<ItemStack, Float>> results = new ArrayList<>();
            for (int i = 0; i < resultList.tagCount(); i++) {
                NBTTagCompound data = resultList.getCompoundTagAt(i);
                float chance = data.hasKey("chance") ? data.getFloat("chance") : 1.0F;
                ItemStack stack = new ItemStack(data.getCompoundTag("Item"));
                results.add(new Tuple<>(stack, chance));
            }
            this.results = ImmutableList.copyOf(results);
        }
    }
}
