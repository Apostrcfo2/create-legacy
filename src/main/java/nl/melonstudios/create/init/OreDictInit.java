package nl.melonstudios.create.init;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import com.melonstudios.melonlib.misc.MetaBlock;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.HashSet;

public final class OreDictInit {
    public static void init() {
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.CASING, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.SHAFT, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.COG_SMALL, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.COG_LARGE, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.GEARBOX, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.GEARSHIFT, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.CLUTCH, true);
        BlockDictionary.registerOre("create:wrenchPickup", BlockInit.HAND_CRANK, true);

        registerOreBlockItem("oreCopper", BlockInit.ORE, 0);
        registerOreBlockItem("oreZinc", BlockInit.ORE, 1);

        registerOreBlockItem("blockCopper", BlockInit.METAL, 1);
        registerOreBlockItem("blockZinc", BlockInit.METAL, 2);
        registerOreBlockItem("blockBrass", BlockInit.METAL, 3);

        registerOre("dustWheat", ItemInit.INGREDIENT, 0);
        registerOre("dough", ItemInit.INGREDIENT, 1);
        registerOre("dustNetherrack", ItemInit.INGREDIENT, 2);
        registerOre("gemRoseQuartz", ItemInit.INGREDIENT, 3);
        registerOre("gemRoseQuartzPolished", ItemInit.INGREDIENT, 4);
        registerOre("dustObsidian", ItemInit.INGREDIENT, 5);
        registerOre("plateObsidian", ItemInit.INGREDIENT, 6);
        registerOre("propeller", ItemInit.INGREDIENT, 7);
        registerOre("whisk", ItemInit.INGREDIENT, 8);
        registerOre("electronTube", ItemInit.INGREDIENT, 10);
        registerOre("ingotCopper", ItemInit.INGREDIENT, 16);
        registerOre("ingotZinc", ItemInit.INGREDIENT, 17);
        registerOre("ingotBrass", ItemInit.INGREDIENT, 18);
        registerOre("nuggetCopper", ItemInit.INGREDIENT, 19);
        registerOre("nuggetZinc", ItemInit.INGREDIENT, 20);
        registerOre("nuggetBrass", ItemInit.INGREDIENT, 21);
        registerOre("plateCopper", ItemInit.INGREDIENT, 22);
        registerOre("plateBrass", ItemInit.INGREDIENT, 23);
        registerOre("plateIron", ItemInit.INGREDIENT, 24);
        registerOre("plateGold", ItemInit.INGREDIENT, 25);
        registerOre("crushedIron", ItemInit.INGREDIENT, 26);
        registerOre("crushedGold", ItemInit.INGREDIENT, 27);
        registerOre("crushedCopper", ItemInit.INGREDIENT, 28);
        registerOre("crushedZinc", ItemInit.INGREDIENT, 29);
    }

    private static void registerOre(String ore, Item item, int meta) {
        OreDictionary.registerOre(ore, new ItemStack(item, 1, meta));
    }
    private static void registerOre(String ore, Block block, int meta) {
        BlockDictionary.registerOre(ore, MetaBlock.of(block, meta));
    }

    private static void registerOreBlockItem(String ore, Block block, int meta) {
        registerOre(ore, block, meta);
        registerOre(ore, Item.getItemFromBlock(block), meta);
    }

    public static final HashSet<String> INGOT_TYPES = new HashSet<>();
    public static final HashSet<String> PLATE_TYPES = new HashSet<>();

    public static void scanMetalTypes() {
        for (String ore : OreDictionary.getOreNames()) {
            if (ore.startsWith("ingot") && ore.length() > "ingot".length()) {
                INGOT_TYPES.add(ore.substring("ingot".length()));
            } else if (ore.startsWith("plate") && ore.length() > "plate".length()) {
                PLATE_TYPES.add(ore.substring("plate".length()));
            }
        }
    }

    private OreDictInit() {
        throw new AssertionError("no");
    }
}
