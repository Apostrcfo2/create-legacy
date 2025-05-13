package nl.melonstudios.create.init;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.BlockCasing;
import nl.melonstudios.create.item.ItemGoggles;
import nl.melonstudios.create.item.ItemIngredient;
import nl.melonstudios.create.util.ModTabs;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ItemInit {
    public static final ArrayList<Item> ITEMS = new ArrayList<>();

    public static final ItemIngredient INGREDIENT = registerItem(new ItemIngredient());
    public static final ItemGoggles GOGGLES = registerItem(new ItemGoggles());

    private static <T extends Item> T registerItem(T item) {
        ITEMS.add(item);
        return item;
    }

    public static void setItemModels() {
        // items
        for (int i = 0; i < ItemIngredient.NAME_LOOKUP.length; i++) {
            CreateLegacy.proxy.setItemModel(INGREDIENT, i, "ingredient/" + ItemIngredient.NAME_LOOKUP[i]);
        }
        CreateLegacy.proxy.setItemModel(GOGGLES);

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
        CreateLegacy.proxy.setItemModel(BlockInit.SPEEDOMETER);
        CreateLegacy.proxy.setItemModel(BlockInit.STRESSOMETER);
    }

    public static final CreativeTabs TAB_CREATE = new ModTabs("create", () -> new ItemStack(BlockInit.COG_SMALL));
    public static final CreativeTabs TAB_CREATE_DECORATIONS = new ModTabs("create.decorations", () -> ItemStack.EMPTY);

    private ItemInit() {
        throw new AssertionError("no");
    }
}
