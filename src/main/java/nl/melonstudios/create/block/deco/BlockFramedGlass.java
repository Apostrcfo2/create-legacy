package nl.melonstudios.create.block.deco;

import com.melonstudios.melonlib.item.IMetaName;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.NonNullList;
import nl.melonstudios.create.init.ItemInit;

import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockFramedGlass extends BlockGlass implements IMetaName {
    public enum Variant implements IStringSerializable {
        DEFAULT("default", 0),
        HORIZONTAL("horizontal", 1),
        VERTICAL("vertical", 2),
        TILES("tiles", 3);

        private final String name;
        private final int id;

        Variant(String name, int id) {
            this.name = name;
            this.id = id;
        }

        @Override
        public String getName() {
            return this.name;
        }
        public int getId() {
            return this.id;
        }

        private static final Variant[] VALUES = {
                DEFAULT, HORIZONTAL, VERTICAL, TILES
        };
        public static Variant byId(int id) {
            if (id < 0 || id >= VALUES.length) return DEFAULT;
            return VALUES[id];
        }
    }

    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockFramedGlass() {
        super(Material.GLASS, false);

        this.setRegistryName("framed_glass");
        this.setUnlocalizedName("create.framed_glass");

        this.setHardness(0.3F);
        this.setSoundType(SoundType.GLASS);
        this.setHarvestLevel("pickaxe", 0);

        this.setCreativeTab(ItemInit.TAB_CREATE_DECORATIONS);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.create.framed_glass_" + Variant.byId(stack.getMetadata()).getName();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < 4; i++) items.add(new ItemStack(this, 1, i));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getId();
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, Variant.byId(meta));
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
    @Override
    public int damageDropped(IBlockState state) {
        return state.getValue(VARIANT).getId();
    }
}
