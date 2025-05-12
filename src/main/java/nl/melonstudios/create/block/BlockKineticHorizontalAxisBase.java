package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockKineticHorizontalAxisBase extends BlockKineticBase {
    public static final PropertyEnum<EnumFacing.Axis> HORIZONTAL_AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public BlockKineticHorizontalAxisBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, HORIZONTAL_AXIS);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing.Axis preferredAxis = getPreferredHorizontalAxis(world, pos);
        if (preferredAxis != null) return this.getDefaultState().withProperty(HORIZONTAL_AXIS, preferredAxis);
        return this.getDefaultState().withProperty(HORIZONTAL_AXIS, placer.getHorizontalFacing().rotateY().getAxis());
    }

    @Nullable
    public static EnumFacing.Axis getPreferredHorizontalAxis(World world, BlockPos pos) {
        EnumFacing preferredSide = null;
        for (EnumFacing side : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(side));
            if (state.getBlock() instanceof IRotate) {
                if (((IRotate)state.getBlock()).hasShaftTowards(world, pos.offset(side), state, side.getOpposite())) {
                    if (preferredSide != null && preferredSide.getAxis() != side.getAxis()) {
                        preferredSide = null;
                        break;
                    } else {
                        preferredSide = side;
                    }
                }
            }
        }
        return preferredSide == null ? null : preferredSide.getAxis();
    }

    @Override
    public EnumFacing.Axis getRotationAxis(IBlockState state) {
        return state.getValue(HORIZONTAL_AXIS);
    }

    @Override
    public boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side) {
        return side.getAxis() == state.getValue(HORIZONTAL_AXIS);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(HORIZONTAL_AXIS,
                rot.rotate(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, state.getValue(HORIZONTAL_AXIS))).getAxis());
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HORIZONTAL_AXIS).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HORIZONTAL_AXIS, EnumFacing.Axis.values()[meta % 3]);
    }
}
