package nl.melonstudios.create.init;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import nl.melonstudios.create.recipe.*;
import nl.melonstudios.create.recipe.sequence.SequenceResult;
import nl.melonstudios.create.recipe.sequence.SequenceStep;
import nl.melonstudios.create.recipe.sequence.SequencedRecipes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class RecipeInit {
    public static void init() {
        int tracker;
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

        //region mechanical press
        PressingRecipes.instance.addRecipe("create:grass", new ItemStack(Blocks.GRASS), new ItemStack(Blocks.GRASS_PATH));
        PressingRecipes.instance.addRecipe("create:sugarcane", new ItemStack(Items.REEDS), new ItemStack(Items.PAPER));
        PressingRecipes.instance.addRecipe("create:pulp",
                new ItemStack(ItemInit.INGREDIENT, 1, 12),
                new ItemStack(ItemInit.INGREDIENT, 1, 13)
        );
        List<String> ingotPlateTuples = findIngotPlateTuples();
        for (String metal : ingotPlateTuples) {
            tracker = 0;
            String ingot = "ingot" + metal;
            String plate = "plate" + metal;
            ItemStack result = OreDictionary.getOres(plate).get(0).copy();
            for (ItemStack stack : OreDictionary.getOres(ingot)) {
                PressingRecipes.instance.addRecipe(
                        "create:oredict" + metal + tracker,
                        stack.copy(),
                        result
                );
            }
        }
        //endregion

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

        //region Vanilla stones cutting
        tracker = 0;
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
        //endregion

        //region Log cutting
        CuttingRecipes.instance.addRecipe("create:efficient_oak",
                new ItemStack(Blocks.LOG, 1, 0),
                new ItemStack(Blocks.PLANKS, 6, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:efficient_spruce",
                new ItemStack(Blocks.LOG, 1, 1),
                new ItemStack(Blocks.PLANKS, 6, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:efficient_birch",
                new ItemStack(Blocks.LOG, 1, 2),
                new ItemStack(Blocks.PLANKS, 6, 2),
                200
        );
        CuttingRecipes.instance.addRecipe("create:efficient_jungle",
                new ItemStack(Blocks.LOG, 1, 3),
                new ItemStack(Blocks.PLANKS, 6, 3),
                200
        );
        CuttingRecipes.instance.addRecipe("create:efficient_acacia",
                new ItemStack(Blocks.LOG2, 1, 0),
                new ItemStack(Blocks.PLANKS, 6, 4),
                200
        );
        CuttingRecipes.instance.addRecipe("create:efficient_dark_oak",
                new ItemStack(Blocks.LOG2, 1, 1),
                new ItemStack(Blocks.PLANKS, 6, 5),
                200
        );
        //endregion

        //region Plank cutting
        CuttingRecipes.instance.addRecipe("create:oak_slab",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_stairs",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.OAK_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_fence",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.OAK_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.OAK_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_door",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Items.OAK_DOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_sign",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_boat",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Items.BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:oak_button",
                new ItemStack(Blocks.PLANKS, 1, 0),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        CuttingRecipes.instance.addRecipe("create:spruce_slab",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_stairs",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.SPRUCE_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_fence",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.SPRUCE_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.SPRUCE_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_door",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Items.SPRUCE_DOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_sign",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_boat",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Items.SPRUCE_BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:spruce_button",
                new ItemStack(Blocks.PLANKS, 1, 1),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        CuttingRecipes.instance.addRecipe("create:birch_slab",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 2),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_stairs",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.BIRCH_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_fence",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.BIRCH_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.BIRCH_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_door",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Items.BIRCH_DOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_sign",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_boat",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Items.BIRCH_BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:birch_button",
                new ItemStack(Blocks.PLANKS, 1, 2),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        CuttingRecipes.instance.addRecipe("create:jungle_slab",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 3),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_stairs",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.JUNGLE_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_fence",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.JUNGLE_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.JUNGLE_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_door",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Items.JUNGLE_DOOR, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_sign",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_boat",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Items.JUNGLE_BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:jungle_button",
                new ItemStack(Blocks.PLANKS, 1, 3),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        CuttingRecipes.instance.addRecipe("create:acacia_slab",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 4),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_stairs",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.ACACIA_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_fence",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.ACACIA_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.ACACIA_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_door",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Items.ACACIA_DOOR, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_sign",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_boat",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Items.ACACIA_BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:acacia_button",
                new ItemStack(Blocks.PLANKS, 1, 4),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );

        CuttingRecipes.instance.addRecipe("create:dark_oak_slab",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.WOODEN_SLAB, 2, 5),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_stairs",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.DARK_OAK_STAIRS, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_fence",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.DARK_OAK_FENCE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_fence_gate",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.DARK_OAK_FENCE_GATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_door",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Items.DARK_OAK_DOOR, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_trapdoor",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.TRAPDOOR, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_sign",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Items.SIGN, 1),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_boat",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Items.DARK_OAK_BOAT, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_pressure_plate",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.WOODEN_PRESSURE_PLATE, 1, 0),
                200
        );
        CuttingRecipes.instance.addRecipe("create:dark_oak_button",
                new ItemStack(Blocks.PLANKS, 1, 5),
                new ItemStack(Blocks.WOODEN_BUTTON, 1, 0),
                200
        );
        //endregion

        CuttingRecipes.instance.addRecipe("create:efficient_shaft",
                new ItemStack(ItemInit.INGREDIENT, 1, 15),
                new ItemStack(BlockInit.SHAFT, 6),
                300
        );

        //region Wooden tools cutting
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("plankWood")) {
            CuttingRecipes.instance.addRecipe("create:sticks" + tracker,
                    stack.copy(),
                    new ItemStack(Items.STICK, 3),
                    200
            );
            CuttingRecipes.instance.addRecipe("create:wooden_sword" + tracker,
                    stack.copy(),
                    new ItemStack(Items.WOODEN_SWORD),
                    400
            );
            CuttingRecipes.instance.addRecipe("create:wooden_shovel" + tracker,
                    stack.copy(),
                    new ItemStack(Items.WOODEN_SHOVEL),
                    400
            );
            CuttingRecipes.instance.addRecipe("create:wooden_pickaxe" + tracker,
                    stack.copy(),
                    new ItemStack(Items.WOODEN_PICKAXE),
                    400
            );
            CuttingRecipes.instance.addRecipe("create:wooden_axe" + tracker,
                    stack.copy(),
                    new ItemStack(Items.WOODEN_AXE),
                    400
            );
            CuttingRecipes.instance.addRecipe("create:wooden_hoe" + tracker,
                    stack.copy(),
                    new ItemStack(Items.WOODEN_HOE),
                    400
            );
            tracker++;
        }
        //endregion

        //region Stonecutting real
        tracker = 0;
        for (ItemStack stack : OreDictionary.getOres("stone")) {
            CuttingRecipes.instance.addRecipe("create:stone_slab" + tracker,
                    stack.copy(),
                    new ItemStack(Blocks.STONE_SLAB, 2, 0),
                    300
            );
            CuttingRecipes.instance.addRecipe("create:stone_bricks" + tracker,
                    stack.copy(),
                    new ItemStack(Blocks.STONEBRICK, 1, 0),
                    300
            );
            CuttingRecipes.instance.addRecipe("create:chiseled_stone_bricks" + tracker,
                    stack.copy(),
                    new ItemStack(Blocks.STONEBRICK, 1, 3),
                    300
            );
            tracker++;
        }
        //endregion

        //region Deploying all over the place

        tracker = 0;
        for (ItemStack log : OreDictionary.getOres("logWood", false)) {
            DeployingRecipes.instance.addRecipe("create:casing_andesite" + tracker,
                    log.copy(),
                    new ItemStack(ItemInit.INGREDIENT, 1, 15),
                    new ItemStack(BlockInit.CASING, 1, 0),
                    DeployerRecipe.InputType.CONSUME
            );
            int tracker2 = 0;
            for (ItemStack copper : OreDictionary.getOres("plateCopper", false)) {
                DeployingRecipes.instance.addRecipe("create:casing_copper" + tracker + "-" + tracker2,
                        log.copy(),
                        copper.copy(),
                        new ItemStack(BlockInit.CASING, 1, 1),
                        DeployerRecipe.InputType.CONSUME
                );
            }
            for (ItemStack brass : OreDictionary.getOres("ingotBrass", false)) {
                DeployingRecipes.instance.addRecipe("create:casing_brass" + tracker + "-" + tracker2,
                        log.copy(),
                        brass.copy(),
                        new ItemStack(BlockInit.CASING, 1, 2),
                        DeployerRecipe.InputType.CONSUME
                );
            }
            tracker++;
        }
        tracker = 0;
        for (ItemStack obsidian : OreDictionary.getOres("plateObsidian", false)) {
            DeployingRecipes.instance.addRecipe("create:casing_train" + tracker,
                    new ItemStack(BlockInit.CASING, 1, 2),
                    obsidian.copy(),
                    new ItemStack(BlockInit.CASING, 1, 3),
                    DeployerRecipe.InputType.CONSUME
            );
        }
        DeployingRecipes.instance.addRecipe("create:rose_quartz_polishing",
                new ItemStack(ItemInit.INGREDIENT, 1, 3),
                new ItemStack(ItemInit.SANDPAPER),
                new ItemStack(ItemInit.INGREDIENT, 1, 4),
                DeployerRecipe.InputType.DAMAGE
        );
        //endregion

        mixing();
        sequences();
    }

    private static void mixing() {
        MixingRecipes recipes = MixingRecipes.instance;
        recipes.addRecipe(
                MixingRecipe.builder()
                        .setInputItems(
                                new ItemStack(Items.SUGAR),
                                new ItemStack(Items.DYE, 1, 0)
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
    }
    private static void sequences() {
        SequencedRecipes recipes = SequencedRecipes.instance;
        recipes.addRecipe("create:test",
                new ItemStack(Items.COAL), new ItemStack(Blocks.COBBLESTONE),
                ImmutableList.of(SequenceStep.pressing(), SequenceStep.deploying(new ItemStack(Items.STICK))), 2,
                new ItemStack(Items.DIAMOND)
        );

        recipes.addRecipe("create:precision_mechanism",
                new ItemStack(ItemInit.INGREDIENT, 1, 25),  new ItemStack(ItemInit.ASSEMBLY, 1, 0),
                ImmutableList.of(
                        SequenceStep.deploying(new ItemStack(BlockInit.COG_SMALL)),
                        SequenceStep.deploying(new ItemStack(BlockInit.COG_LARGE)),
                        SequenceStep.deploying(new ItemStack(Items.IRON_NUGGET))
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
                if (!OreDictionary.getOres(s).isEmpty()) {
                    ingots.add(s.substring(5));
                }
            }
        }
        for (String s : OreDictionary.getOreNames()) {
            if (s.startsWith("plate") && s.length() > 5) {
                if (!OreDictionary.getOres(s).isEmpty()) {
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
}
