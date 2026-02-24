package nl.melonstudios.create.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.melonstudios.melonlib.MelonLib;
import com.melonstudios.melonlib.recipe.IRecipeAccessor;
import com.melonstudios.melonlib.recipe.Ingredient;
import com.melonstudios.melonlib.recipe.RecipeRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.recipe.*;
import nl.melonstudios.create.recipe.sequence.SequenceRecipe;
import nl.melonstudios.create.recipe.sequence.SequenceResult;
import nl.melonstudios.create.recipe.sequence.SequenceStep;
import nl.melonstudios.create.recipe.sequence.SequencedRecipes;
import nl.melonstudios.create.recipe.server.CuttingRecipes;
import nl.melonstudios.create.recipe.server.DeployerRecipes;
import nl.melonstudios.create.recipe.server.MixingRecipes;
import nl.melonstudios.create.recipe.server.PressingRecipes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class RecipeInit {
    public static void init() {
        for (ItemStack ore : OreDictionary.getOres("crushedIron")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(Items.IRON_INGOT), 0.3F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedGold")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(Items.GOLD_INGOT), 0.3F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedCopper")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(ItemInit.INGREDIENT, 1, 16), 0.3F);
        }
        for (ItemStack ore : OreDictionary.getOres("crushedZinc")) {
            FurnaceRecipes.instance().addSmeltingRecipe(ore.copy(), new ItemStack(ItemInit.INGREDIENT, 1, 17), 0.3F);
        }

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockInit.ORE, 1, 0),
                new ItemStack(ItemInit.INGREDIENT, 1, 16), 0.3F);
        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(BlockInit.ORE, 1, 1),
                new ItemStack(ItemInit.INGREDIENT, 1, 17), 0.3F);

        FurnaceRecipes.instance().addSmeltingRecipe(new ItemStack(Blocks.SOUL_SAND, 1),
                new ItemStack(BlockInit.ORESTONE, 1, 5), 0.05F);

        /*

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

        */

        pressing();
        cutting();
        mixing();
        deploying();
        sequences();
    }

    private static void pressing() {
        PressingRecipes recipes = PressingRecipes.instance;

        recipes.addRecipe("create:grass",
                Ingredient.of("grass"),
                new ItemStack(Blocks.GRASS_PATH)
        );
        recipes.addRecipe("create:sugarcane",
                Ingredient.of("sugarcane"),
                new ItemStack(Items.PAPER)
        );
        recipes.addRecipe("create:pulp",
                Ingredient.of(new ItemStack(ItemInit.INGREDIENT, 1, 12), false),
                new ItemStack(ItemInit.INGREDIENT, 1, 13)
        );
        List<String> ingotPlateTuples = findIngotPlateTuples();
        for (String metal : ingotPlateTuples) {
            String ingot = "ingot" + metal;
            String plate = "plate" + metal;
            ItemStack result = OreDictionary.getOres(plate).get(0).copy();
            recipes.addRecipe(
                    "create:" + metal + "_ingot",
                    Ingredient.of(ingot),
                    result
            );
        }
    }
    private static void cutting() {
        CuttingRecipes recipes = CuttingRecipes.instance;

        recipes.addRecipe("create:stone_slab",
                Ingredient.of("stone"),
                new ItemStack(Blocks.STONE_SLAB, 2, 0),
                300
        );
        recipes.addRecipe("create:stone_bricks",
                Ingredient.of("stone"),
                new ItemStack(Blocks.STONEBRICK, 1, 0),
                300
        );
        recipes.addRecipe("create:chiseled_stone_bricks",
                Ingredient.of("stone"),
                new ItemStack(Blocks.STONEBRICK, 1, 3),
                300
        );
        
        int tracker;

        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneAndesitePolished")) {
            recipes.addRecipe("create:auto/stoneAndesitePolished" + tracker++,
                    Ingredient.of("stoneAndesite"),
                    stack.copy(),
                    300
            );
        }
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneDioritePolished")) {
            recipes.addRecipe("create:auto/stoneDioritePolished" + tracker++,
                    Ingredient.of("stoneDiorite"),
                    stack.copy(),
                    300
            );
        }
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stoneGranitePolished")) {
            recipes.addRecipe("create:auto/stoneGranitePolished" + tracker++,
                    Ingredient.of("stoneGranite"),
                    stack.copy(),
                    300
            );
        }

        recipes.addRecipe("create:efficient_oak",
                Ingredient.of(new ItemStack(Blocks.LOG, 1, 0), false),
                new ItemStack(Blocks.PLANKS, 6, 0),
                200
        );
        recipes.addRecipe("create:efficient_spruce",
                Ingredient.of(new ItemStack(Blocks.LOG, 1, 1), false),
                new ItemStack(Blocks.PLANKS, 6, 1),
                200
        );
        recipes.addRecipe("create:efficient_birch",
                Ingredient.of(new ItemStack(Blocks.LOG, 1, 2), false),
                new ItemStack(Blocks.PLANKS, 6, 2),
                200
        );
        recipes.addRecipe("create:efficient_jungle",
                Ingredient.of(new ItemStack(Blocks.LOG, 1, 3), false),
                new ItemStack(Blocks.PLANKS, 6, 3),
                200
        );
        recipes.addRecipe("create:efficient_acacia",
                Ingredient.of(new ItemStack(Blocks.LOG2, 1, 0), false),
                new ItemStack(Blocks.PLANKS, 6, 4),
                200
        );
        recipes.addRecipe("create:efficient_dark_oak",
                Ingredient.of(new ItemStack(Blocks.LOG2, 1, 1), false),
                new ItemStack(Blocks.PLANKS, 6, 5),
                200
        );

        //region Plank cutting
        recipes.addRecipe("create:oak_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 0),
                200
        );
        recipes.addRecipe("create:oak_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.OAK_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.OAK_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.OAK_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Items.OAK_DOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:oak_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Items.BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:oak_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 0), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        recipes.addRecipe("create:spruce_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 1),
                200
        );
        recipes.addRecipe("create:spruce_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.SPRUCE_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.SPRUCE_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Items.SPRUCE_DOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:spruce_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Items.SPRUCE_BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:spruce_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 1), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        recipes.addRecipe("create:birch_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 2),
                200
        );
        recipes.addRecipe("create:birch_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.BIRCH_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.BIRCH_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.BIRCH_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Items.BIRCH_DOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:birch_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Items.BIRCH_BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:birch_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 2), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        recipes.addRecipe("create:jungle_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 3),
                200
        );
        recipes.addRecipe("create:jungle_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.JUNGLE_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.JUNGLE_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Items.JUNGLE_DOOR, 1),
                200
        );
        recipes.addRecipe("create:jungle_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:jungle_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Items.JUNGLE_BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:jungle_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 3), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        recipes.addRecipe("create:acacia_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 4),
                200
        );
        recipes.addRecipe("create:acacia_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.ACACIA_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.ACACIA_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.ACACIA_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Items.ACACIA_DOOR, 1),
                200
        );
        recipes.addRecipe("create:acacia_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:acacia_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Items.ACACIA_BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:acacia_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 4), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        recipes.addRecipe("create:dark_oak_slab",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 5),
                200
        );
        recipes.addRecipe("create:dark_oak_stairs",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.DARK_OAK_STAIRS, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_fence",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.DARK_OAK_FENCE, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_fence_gate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_door",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Items.DARK_OAK_DOOR, 1),
                200
        );
        recipes.addRecipe("create:dark_oak_trapdoor",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_sign",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Items.SIGN, 1),
                200
        );
        recipes.addRecipe("create:dark_oak_boat",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Items.DARK_OAK_BOAT, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_pressure_plate",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        recipes.addRecipe("create:dark_oak_button",
                Ingredient.of(new ItemStack(Blocks.PLANKS, 1, 5), false),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );
        //endregion

        recipes.addRecipe("create:efficient_shaft",
                Ingredient.of(new ItemStack(ItemInit.INGREDIENT, 1, 15), false),
                new ItemStack(BlockInit.SHAFT, 6),
                300
        );

        //region Wooden tools cutting
        recipes.addRecipe("create:sticks",
                Ingredient.of("plankWood"),
                new ItemStack(Items.STICK, 3),
                200
        );
        recipes.addRecipe("create:wooden_sword",
                Ingredient.of("plankWood"),
                new ItemStack(Items.WOODEN_SWORD),
                400
        );
        recipes.addRecipe("create:wooden_shovel",
                Ingredient.of("plankWood"),
                new ItemStack(Items.WOODEN_SHOVEL),
                400
        );
        recipes.addRecipe("create:wooden_pickaxe",
                Ingredient.of("plankWood"),
                new ItemStack(Items.WOODEN_PICKAXE),
                400
        );
        recipes.addRecipe("create:wooden_axe",
                Ingredient.of("plankWood"),
                new ItemStack(Items.WOODEN_AXE),
                400
        );
        recipes.addRecipe("create:wooden_hoe",
                Ingredient.of("plankWood"),
                new ItemStack(Items.WOODEN_HOE),
                400
        );
        //endregion
    }
    private static void mixing() {
        MixingRecipes recipes = MixingRecipes.instance;
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Items.SUGAR),
                                new ItemStack(Items.DYE, 1, 3)
                        )
                        .setInputFluids(
                                new FluidStack(FluidInit.milk(), 250)
                        )
                        .setOutputItems()
                        .setOutputFluid(
                                new FluidStack(FluidInit.chocolate(), 250)
                        )
                        .build("create:chocolate")
        );
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Blocks.LEAVES, 1, OreDictionary.WILDCARD_VALUE)
                        )
                        .setInputFluids(
                                new FluidStack(FluidRegistry.WATER, 250),
                                new FluidStack(FluidInit.milk(), 250)
                        )
                        .setOutputItems()
                        .setOutputFluid(
                                new FluidStack(FluidInit.tea(), 500)
                        )
                        .build("create:tea")
        );
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Blocks.LEAVES2, 1, OreDictionary.WILDCARD_VALUE)
                        )
                        .setInputFluids(
                                new FluidStack(FluidRegistry.WATER, 250),
                                new FluidStack(FluidInit.milk(), 250)
                        )
                        .setOutputItems()
                        .setOutputFluid(
                                new FluidStack(FluidInit.tea(), 500)
                        )
                        .build("create:tea2")
        );
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Blocks.STONE, 1, 5),
                                new ItemStack(Items.IRON_NUGGET)
                        )
                        .setOutputItems(
                                new ItemStack(ItemInit.INGREDIENT, 1, 15)
                        )
                        .build("create:andesite_alloy")
        );
        int tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("nuggetZinc")) {
            recipes.addRecipe(
                    MixingRecipe.builder()
                            .setInputItems(
                                    new ItemStack(Blocks.STONE, 1, 5),
                                    stack.copy()
                            )
                            .setOutputItems(
                                    new ItemStack(ItemInit.INGREDIENT, 1, 15)
                            )
                            .build("create:andesite_alloy_zinc" + tracker++)
            );
        }
        tracker = 0;
        for (ItemStack zinc : OreDictionary.getOres("ingotZinc")) {
            for (ItemStack copper : OreDictionary.getOres("ingotCopper")) {
                recipes.addRecipe(
                        MixingRecipe.builder()
                                .setInputItems(
                                        zinc.copy(),
                                        copper.copy()
                                )
                                .setOutputItems(
                                        new ItemStack(ItemInit.INGREDIENT, 2, 18)
                                )
                                .setRequiredHeat(1)
                                .build("create:brass" + tracker++)
                );
            }
        }
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Blocks.SAPLING, 4, OreDictionary.WILDCARD_VALUE)
                        )
                        .setInputFluids(
                                new FluidStack(FluidRegistry.WATER, 250)
                        )
                        .setOutputItems(
                                new ItemStack(ItemInit.INGREDIENT, 1, 12)
                        )
                        .build("create:pulp")
        );
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(ItemInit.INGREDIENT, 1, 0)
                        )
                        .setInputFluids(
                                new FluidStack(FluidRegistry.WATER, 1000)
                        )
                        .setOutputItems(
                                new ItemStack(ItemInit.INGREDIENT, 1, 1)
                        )
                        .build("create:dough")
        );
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Blocks.COBBLESTONE, 1, OreDictionary.WILDCARD_VALUE)
                        )
                        .setOutputFluid(new FluidStack(FluidRegistry.LAVA, 50))
                        .setRequiredHeat(2)
                        .build("create:cobblestone_melting")
        );
    }
    private static void deploying() {
        DeployerRecipes recipes = DeployerRecipes.instance;

        recipes.addRecipe("create:casing_andesite",
                Ingredient.of("logWood"),
                Ingredient.of(new ItemStack(ItemInit.INGREDIENT, 1, 15), false),
                new ItemStack(BlockInit.CASING,1, 0)
        );
        recipes.addRecipe("create:casing_copper",
                Ingredient.of("logWood"),
                Ingredient.of("plateCopper"),
                new ItemStack(BlockInit.CASING, 1, 1)
        );
        recipes.addRecipe("create:casing_brass",
                Ingredient.of("logWood"),
                Ingredient.of("ingotBrass"),
                new ItemStack(BlockInit.CASING, 1, 2)
        );
        recipes.addRecipe("create:casing_train",
                Ingredient.of(new ItemStack(BlockInit.CASING, 1, 2), false),
                Ingredient.of("plateObsidian"),
                new ItemStack(BlockInit.CASING, 1, 3)
        );
        recipes.addRecipe("create:rose_quartz_polishing",
                Ingredient.of("gemRoseQuartz"),
                Ingredient.of(new ItemStack(ItemInit.SANDPAPER), false),
                new ItemStack(ItemInit.INGREDIENT, 1, 4),
                DeployerRecipe.InputType.DAMAGE
        );
    }
    private static void sequences() {
        SequencedRecipes recipes = SequencedRecipes.instance;

        recipes.addRecipe("create:test",
                Ingredient.of(new ItemStack(Items.COAL), false),
                new ItemStack(Blocks.COBBLESTONE),
                ImmutableList.of(SequenceStep.pressing(), SequenceStep.deploying(new ItemStack(Items.STICK))), 2,
                new ItemStack(Items.DIAMOND)
        );

        recipes.addRecipe("create:precision_mechanism",
                Ingredient.of("plateGold"),
                new ItemStack(ItemInit.ASSEMBLY, 1, 0),
                ImmutableList.of(
                        SequenceStep.deploying(new ItemStack(BlockInit.COG_SMALL, 1)),
                        SequenceStep.deploying(new ItemStack(BlockInit.COG_LARGE, 1)),
                        SequenceStep.deploying(Ingredient.of("nuggetIron"))
                ), 5,
                new SequenceResult(new ItemStack(ItemInit.INGREDIENT, 1, 14), 85.0F,
                        new ItemStack(ItemInit.INGREDIENT, 1, 25), 5.4F,
                        new ItemStack(ItemInit.INGREDIENT, 1, 15), 5.4F,
                        new ItemStack(BlockInit.COG_SMALL), 3.4F,
                        new ItemStack(BlockInit.SHAFT), 1.4F,
                        new ItemStack(ItemInit.INGREDIENT, 1, 27), 1.4F,
                        new ItemStack(Items.GOLD_NUGGET), 1.4F,
                        new ItemStack(Items.IRON_INGOT), 0.7F,
                        new ItemStack(Items.CLOCK), 0.7F
                )
        );
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
                if (!OreDictionary.getOres(s, false).isEmpty()) {
                    ingots.add(s.substring(5));
                }
            }
        }
        for (String s : OreDictionary.getOreNames()) {
            if (s.startsWith("plate") && s.length() > 5) {
                if (!OreDictionary.getOres(s, false).isEmpty()) {
                    plates.add(s.substring(5));
                }
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

    public static IRecipeAccessor<PressingRecipe> getPressingRecipes() {
        return RecipeRegistry.getRecipeAccessor("create:pressing");
    }
    public static IRecipeAccessor<CuttingRecipe> getCuttingRecipes() {
        return MelonLib.proxy.getRecipeAccessor("create:cutting");
    }
    public static IRecipeAccessor<DeployerRecipe> getDeployerRecipes() {
        return MelonLib.proxy.getRecipeAccessor("create:deploying");
    }
    public static IRecipeAccessor<SequenceRecipe> getSequenceRecipes() {
        return MelonLib.proxy.getRecipeAccessor("create:sequence");
    }
}
