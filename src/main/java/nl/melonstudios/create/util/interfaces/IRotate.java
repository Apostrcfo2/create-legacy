package nl.melonstudios.create.util.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IRotate {
    static IRotate is(IBlockState state) {
        return state.getBlock() instanceof IRotate ? (IRotate) state.getBlock() : null;
    }
    boolean hasShaftTowards(World world, BlockPos pos, IBlockState state, EnumFacing side);
    EnumFacing.Axis getRotationAxis(IBlockState state);
}
