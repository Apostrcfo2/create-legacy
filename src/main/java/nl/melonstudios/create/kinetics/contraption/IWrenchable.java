package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IWrenchable {
    boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ);
}
