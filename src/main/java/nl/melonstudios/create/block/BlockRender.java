package nl.melonstudios.create.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.util.EnumRenderPart;
import nl.melonstudios.create.util.interfaces.IStateFindable;

@SuppressWarnings("all")
public final class BlockRender extends Block {
    public static final PropertyEnum<EnumRenderPart> RENDER_PART = PropertyEnum.create("render_part", EnumRenderPart.class);
    public BlockRender() {
        super(Material.CIRCUITS);

        this.setRegistryName("render");
        this.setUnlocalizedName("render");
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, RENDER_PART);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(RENDER_PART).getID() & 15;
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return byID(meta);
    }

    public static IBlockState getState() {
        return BlockInit.RENDER.getDefaultState();
    }
    public static IBlockState byName(String name) {
        return getState().withProperty(RENDER_PART, EnumRenderPart.byName(name));
    }
    public static IBlockState byID(int id) {
        return getState().withProperty(RENDER_PART, EnumRenderPart.byID(id));
    }
    public static IBlockState byEnum(EnumRenderPart erp) {
        return getState().withProperty(RENDER_PART, erp);
    }
}