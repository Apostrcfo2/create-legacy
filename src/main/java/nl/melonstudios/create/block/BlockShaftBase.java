package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.AABB;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.TileEntityShaft;
import nl.melonstudios.create.util.BlockProperties;

import javax.annotation.Nullable;

public abstract class BlockShaftBase extends BlockKineticRotatedPillarBase implements ITileEntityProvider {
    public BlockShaftBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityShaft();
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side.getAxis() == state.getValue(AXIS);
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        switch (state.getValue(AXIS)) {
            case X: return BlockProperties.SHAFT_X_AABB;
            case Y: return BlockProperties.SHAFT_Y_AABB;
            case Z: return BlockProperties.SHAFT_Z_AABB;
            default:return AABB.FULL_BLOCK_AABB;
        }
    }
}
