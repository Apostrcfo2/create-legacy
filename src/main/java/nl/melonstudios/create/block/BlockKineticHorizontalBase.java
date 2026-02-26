package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
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

@SuppressWarnings("deprecation")
public abstract class BlockKineticHorizontalBase extends BlockKineticBase {
    public static final PropertyDirection FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockKineticHorizontalBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState()
                .withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

    @Nullable
    public EnumFacing getPreferredHorizontalFacing(World world, BlockPos pos, EnumFacing defaultFacing) {
        EnumFacing prefferedSide = null;
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            IBlockState blockState = world
                    .getBlockState(pos
                            .offset(side));
            if (blockState.getBlock() instanceof IRotate) {
                if (((IRotate) blockState.getBlock()).hasShaftTowards(world, pos
                        .offset(side), blockState, side.getOpposite()))
                    if (prefferedSide != null && prefferedSide.getAxis() != side.getAxis()) {
                        prefferedSide = null;
                        break;
                    } else {
                        prefferedSide = side;
                    }
            }
        }
        return prefferedSide;
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }
}
