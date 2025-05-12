package nl.melonstudios.create.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import nl.melonstudios.create.util.EnumRenderPart;

public class BlockRender extends Block {
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
        return state.getValue(RENDER_PART).ordinal();
    }
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(RENDER_PART, EnumRenderPart.byID(meta));
    }
}
