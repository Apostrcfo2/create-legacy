package nl.melonstudios.create.init;

import com.google.common.collect.Sets;
import com.melonstudios.melonlib.blockdict.BlockDictionary;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.recipe.CuttingRecipes;

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

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Blocks.SOUL_SAND, 1),
                new ItemStack(BlockInit.ORESTONE, 1, 5), 0.05F);

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    if (j != k) {
                        CuttingRecipes.instance.addRecipe(
                                "create:orestone_" + i + "_as_" + j + "_to_" + k,
                                new ItemStack(getOrestone(j), 1, i),
                                new ItemStack(getOrestone(k), 1, i),
                                300
                        );
                    }
                }
            }
        }

        int tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneAndesite")) {
            for (ItemStack result : OreDictionary.getOres("stoneAndesitePolished")) {
                CuttingRecipes.instance.addRecipe("create:andesite_cutting" + (tracker++),
                        stack.copy(),
                        result.copy(),
                        300
                );
            }
        }
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneDiorite")) {
            for (ItemStack result : OreDictionary.getOres("stoneDioritePolished")) {
                CuttingRecipes.instance.addRecipe("create:diorite_cutting" + (tracker++),
                        stack.copy(),
                        result.copy(),
                        300
                );
            }
        }
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneGranite")) {
            for (ItemStack result : OreDictionary.getOres("stoneGranitePolished")) {
                CuttingRecipes.instance.addRecipe("create:granite_cutting" + (tracker++),
                        stack.copy(),
                        result.copy(),
                        300
                );
            }
        }
    }

    private static Item getOrestone(int id) {
        switch (id) {
            case 1: return Item.getItemFromBlock(BlockInit.ORESTONE_CUT);
            case 2: return Item.getItemFromBlock(BlockInit.ORESTONE_POLISHED);
            case 3: return Item.getItemFromBlock(BlockInit.ORESTONE_BRICKS);
            case 4: return Item.getItemFromBlock(BlockInit.ORESTONE_BRICKS_FANCY);
            case 5: return Item.getItemFromBlock(BlockInit.ORESTONE_LAYERED);
            case 6: return Item.getItemFromBlock(BlockInit.ORESTONE_PILLAR_Y);
            default:return Item.getItemFromBlock(BlockInit.ORESTONE);
        }
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
