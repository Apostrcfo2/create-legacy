package nl.melonstudios.create.block;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import mcp.MethodsReturnNonnullByDefault;
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
import nl.melonstudios.create.util.Utils;
import nl.melonstudios.create.util.interfaces.IRotate;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class BlockKineticDirectionalBase extends BlockKineticBase {
    public static final PropertyDirection FACING = BlockStateProperties.FACING;

    public BlockKineticDirectionalBase(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }

    @Nullable
    public EnumFacing getPreferredFacing(World world, BlockPos pos) {
        EnumFacing preferredSide = null;
        for (EnumFacing side : EnumFacing.VALUES) {
            IBlockState state = world.getBlockState(pos.offset(side));
            IRotate rotate = IRotate.is(state);
            if (rotate != null) {
                if (rotate.hasShaftTowards(world, pos.offset(side), state, side.getOpposite())) {
                    if (preferredSide != null && preferredSide.getAxis() != side.getAxis()) {
                        preferredSide = null;
                        break;
                    } else {
                        preferredSide = side;
                    }
                }
            }
        }
        return preferredSide;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        EnumFacing preferred = this.getPreferredFacing(world, pos);
        if (preferred == null || placer.isSneaking()) {
            EnumFacing look = EnumFacing.getDirectionFromEntityLiving(pos, placer);
            return this.getDefaultState().withProperty(FACING, placer.isSneaking() ? facing.getOpposite() : look);
        }
        return this.getDefaultState().withProperty(FACING, preferred.getOpposite());
    }

    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
        return state.withProperty(FACING, mirrorIn.mirror(state.getValue(FACING)));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.VALUES[(meta & 7) % 6]);
    }

    protected boolean disabledWrenchRotation = false;
    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state,
                              EnumFacing side, float hitX, float hitY, float hitZ) {
        if (this.disabledWrenchRotation || state.getValue(FACING).getAxis().apply(side)) return false;
        EnumFacing rotated = state.getValue(FACING).rotateAround(side.getAxis());
        Utils.setBlockKineticTESafe(world, pos, state.withProperty(FACING, rotated), 3);
        return true;
    }
}
