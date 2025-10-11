package nl.melonstudios.create.extensions;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IExtensionBlock {
    boolean create$isSideSticky(IBlockState state, EnumFacing side);
    void create$addStickyLocations(World world, BlockPos pos, IBlockState state, EnumFacing side, List<BlockPos> positions);
}
