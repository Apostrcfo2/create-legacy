package nl.melonstudios.create.extensions;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IExtensionBlock {
    default boolean create$isSideSticky(IBlockState state, EnumFacing side) {
        return ((Block)this).isStickyBlock(state);
    }
    default void create$addStickyLocations(World world, BlockPos pos, IBlockState state, List<BlockPos> positions) {
        for (EnumFacing side : EnumFacing.VALUES) {
            if (this.create$isSideSticky(state, side)) {
                positions.add(pos.offset(side));
            }
        }
    }
}
