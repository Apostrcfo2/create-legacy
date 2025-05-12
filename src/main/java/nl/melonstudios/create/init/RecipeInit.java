package nl.melonstudios.create.init;

import com.google.common.collect.Sets;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class RecipeInit {
    public static void init() {
        for (ItemStack ore : OreDictionary.getOres("crushedIron")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(Items.IRON_INGOT), 0.1F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedGold")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(Items.GOLD_INGOT), 0.1F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedCopper")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(ItemInit.INGREDIENT, 1, 16), 0.1F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedZinc")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(ItemInit.INGREDIENT, 1, 17), 0.1F);
        }

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockInit.ORE, 1, 0),
                new ItemStack(ItemInit.INGREDIENT, 1, 16), 0.3F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockInit.ORE, 1, 1),
                new ItemStack(ItemInit.INGREDIENT, 1, 17), 0.3F);
    }

    private static final HashSet<String> METAL_BLACKLIST = Sets.newHashSet("Obsidian");
    private static List<String> findIngotPlateTuples() {
        HashSet<String> ingots = new HashSet<>();
        HashSet<String> plates = new HashSet<>();
        ArrayList<String> match = new ArrayList<>();

        for (String s : OreDictionary.getOreNames()) {
            if (s.startsWith("ingot") && s.length() > 5) {
                ingots.add(s.substring(5));
            }
        }
        for (String s : OreDictionary.getOreNames()) {
            if (s.startsWith("plate") && s.length() > 5) {
                plates.add(s.substring(5));
            }
        }

        for (String plate : plates) {
            if (!METAL_BLACKLIST.contains(plate) && ingots.contains(plate)) match.add(plate);
        }

        return match;
    }

    private RecipeInit() {
        throw new AssertionError("no");
    }
}
