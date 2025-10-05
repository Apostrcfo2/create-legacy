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
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockKineticRotatedPillarBase extends BlockKineticBase {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = BlockStateProperties.AXIS;
    public BlockKineticRotatedPillarBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_90:
            case COUNTERCLOCKWISE_90:
                switch (state.getValue(AXIS)) {
                    case X: return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z: return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:return state;
                }
            default:
                return state;
        }
    }

    @Nullable
    public static EnumFacing.Axis getPreferredAxis(World world, BlockPos pos) {
        EnumFacing.Axis preferredAxis = null;
        for (EnumFacing side : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(side));
            if (state.getBlock() instanceof IRotate) {
                if (((IRotate) state.getBlock()).hasShaftTowards(world, pos.offset(side), state, side.getOpposite())) {
                    if (preferredAxis != null && preferredAxis != side.getAxis()) {
                        preferredAxis = null;
                        break;
                    } else {
                        preferredAxis = side.getAxis();
                    }
                }
            }
        }
        return preferredAxis;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing.Axis preferredAxis = getPreferredAxis(world, pos);
        if (preferredAxis != null && !placer.isSneaking()) {
            return this.getDefaultState().withProperty(AXIS, preferredAxis);
        }
        return this.getDefaultState().withProperty(AXIS, preferredAxis != null && placer.isSneaking() ?
                facing.getAxis() : EnumFacing.getDirectionFromEntityLiving(pos, placer).getAxis());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int axisMeta = meta & 3;
        return this.getDefaultState().withProperty(AXIS, EnumFacing.Axis.values()[axisMeta == 3 ? 0 : axisMeta]);
    }

    protected boolean disabledWrenchRotation = false;
    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state,
                              EnumFacing side, float hitX, float hitY, float hitZ) {
        if (this.disabledWrenchRotation || state.getValue(AXIS).apply(side)) return false;
        EnumFacing.Axis rotated = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, state.getValue(AXIS))
                .rotateAround(side.getAxis()).getAxis();
        world.setBlockState(pos, state.withProperty(AXIS, rotated));
        return true;
    }
}
