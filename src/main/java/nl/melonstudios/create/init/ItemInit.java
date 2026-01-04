package nl.melonstudios.create.init;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.BlockCasing;
import nl.melonstudios.create.block.state.EnumOrestoneVariant;
import nl.melonstudios.create.item.*;
import nl.melonstudios.create.util.ModTabs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ItemInit {
    public static final CreativeTabs TAB_CREATE = new ModTabs("create", () -> new ItemStack(BlockInit.COG_SMALL));
    public static final CreativeTabs TAB_CREATE_DECORATIONS = new ModTabs("create.decorations", () -> new ItemStack(BlockInit.ORESTONE));

    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final ItemWrench WRENCH = (ItemWrench)
            registerItem(new ItemWrench()
            .setRegistryName("wrench").setUnlocalizedName("create.wrench"));
    public static final ItemIngredient INGREDIENT = registerItem(new ItemIngredient());
    public static final ItemGoggles GOGGLES = registerItem(new ItemGoggles());
    public static final ItemSandpaper SANDPAPER = registerItem(new ItemSandpaper());
    public static final ItemTreeFertilizer TREE_FERTILIZER = (ItemTreeFertilizer)
            registerItem(new ItemTreeFertilizer()
            .setRegistryName("tree_fertilizer").setUnlocalizedName("create.tree_fertilizer"));
    public static final ItemGlue SUPER_GLUE = (ItemGlue)
            registerItem(new ItemGlue(256)
            .setRegistryName("superglue").setUnlocalizedName("create.superglue"));

    private static <T extends Item> T registerItem(T item) {
        ITEMS.add(item);
        return item;
    }

    public static void setItemModels() {
        // items
        CreateLegacy.proxy.setItemModel(WRENCH);
        for (int i = 0; i < ItemIngredient.NAME_LOOKUP.length; i++) {
            CreateLegacy.proxy.setItemModel(INGREDIENT, i, "ingredient/" + ItemIngredient.NAME_LOOKUP[i]);
        }
        CreateLegacy.proxy.setItemModel(GOGGLES);
        CreateLegacy.proxy.setItemModel(SANDPAPER);
        CreateLegacy.proxy.setItemModel(TREE_FERTILIZER);
        CreateLegacy.proxy.setItemModel(SUPER_GLUE);

        // blocks
        CreateLegacy.proxy.setItemModel(BlockInit.ORE, 0, "ore_copper");
        CreateLegacy.proxy.setItemModel(BlockInit.ORE, 1, "ore_zinc");

        CreateLegacy.proxy.setItemModel(BlockInit.METAL, 0, "block_andesite_alloy");
        CreateLegacy.proxy.setItemModel(BlockInit.METAL, 1, "block_copper");
        CreateLegacy.proxy.setItemModel(BlockInit.METAL, 2, "block_zinc");
        CreateLegacy.proxy.setItemModel(BlockInit.METAL, 3, "block_brass");

        for (int i = 0; i < 4; i++) {
            CreateLegacy.proxy.setItemModel(BlockInit.CASING, i, "casing_" + BlockCasing.Variant.byID(i).getName());
        }

        CreateLegacy.proxy.setItemModel(BlockInit.SHAFT);
        CreateLegacy.proxy.setItemModel(BlockInit.COG_SMALL);
        CreateLegacy.proxy.setItemModel(BlockInit.COG_LARGE);
        CreateLegacy.proxy.setItemModel(BlockInit.GEARBOX, 0, "gearbox");
        CreateLegacy.proxy.setItemModel(BlockInit.GEARBOX, 1, "gearbox_vertical");
        CreateLegacy.proxy.setItemModel(BlockInit.GEARSHIFT);
        CreateLegacy.proxy.setItemModel(BlockInit.CLUTCH);
        CreateLegacy.proxy.setItemModel(BlockInit.HAND_CRANK);
        CreateLegacy.proxy.setItemModel(BlockInit.WATER_WHEEL);
        CreateLegacy.proxy.setItemModel(BlockInit.TURNTABLE);
        CreateLegacy.proxy.setItemModel(BlockInit.BEARING);
        CreateLegacy.proxy.setItemModel(BlockInit.BEARING_WINDMILL);
        CreateLegacy.proxy.setItemModel(BlockInit.CHASSIS_RADIAL);
        CreateLegacy.proxy.setItemModel(BlockInit.CHASSIS_LINEAR, 0, "chassis_linear");
        CreateLegacy.proxy.setItemModel(BlockInit.CHASSIS_LINEAR, 1, "chassis_linear_secondary");
        CreateLegacy.proxy.setItemModel(BlockInit.SAIL_ITEM);
        CreateLegacy.proxy.setItemModel(BlockInit.SPEEDOMETER);
        CreateLegacy.proxy.setItemModel(BlockInit.STRESSOMETER);
        CreateLegacy.proxy.setItemModel(BlockInit.PRESS);
        CreateLegacy.proxy.setItemModel(BlockInit.DRILL);
        CreateLegacy.proxy.setItemModel(BlockInit.SAW);
        CreateLegacy.proxy.setItemModel(BlockInit.MILLSTONE);
        CreateLegacy.proxy.setItemModel(BlockInit.DEPOT);
        CreateLegacy.proxy.setItemModel(BlockInit.CHUTE);

        for (int i = 0; i < 7; i++) {
            String type = EnumOrestoneVariant.byId(i).getName();
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE, i ,"orestone/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_CUT, i ,"orestone/cut/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_POLISHED, i, "orestone/polished/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_BRICKS, i ,"orestone/bricks/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_BRICKS_FANCY, i ,"orestone/bricks_fancy/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_LAYERED, i, "orestone/layered/" + type);
            CreateLegacy.proxy.setItemModel(BlockInit.ORESTONE_PILLAR_Y, i, "orestone/pillar/" + type);
        }
    }

    private ItemInit() {
        throw new AssertionError("no");
    }
}
