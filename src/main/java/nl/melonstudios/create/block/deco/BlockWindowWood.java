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
import nl.melonstudios.create.util.BlockProperties;

import java.util.Random;

@SuppressWarnings("deprecation")
public class BlockWindowWood extends BlockGlass implements IMetaName {
    public enum Variant implements IStringSerializable {
        OAK("oak", 0),
        SPRUCE("spruce", 1),
        BIRCH("birch", 2),
        JUNGLE("jungle", 3),
        ACACIA("acacia", 4),
        DARK_OAK("dark_oak", 5);

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
                OAK, SPRUCE, BIRCH, JUNGLE, ACACIA, DARK_OAK
        };

        public static Variant byId(int id) {
            if (id < 0 || id >= VALUES.length) return OAK;
            return VALUES[id];
        }
    }

    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);

    public BlockWindowWood() {
        super(Material.GLASS, false);

        this.setRegistryName("window_wood");
        this.setUnlocalizedName("create.window_wood");

        this.setHardness(BlockProperties.WOOD_HARDNESS);
        this.setSoundType(SoundType.WOOD);
        this.setHarvestLevel("axe", -1);

        this.setCreativeTab(ItemInit.TAB_CREATE_DECORATIONS);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, VARIANT);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile.create.window_" + Variant.byId(stack.getMetadata()).getName();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < 6; i++) items.add(new ItemStack(this, 1, i));
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
