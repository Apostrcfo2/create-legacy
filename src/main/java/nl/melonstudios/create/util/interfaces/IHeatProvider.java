package nl.melonstudios.create.util.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Interface for blocks that provide heat, such as the blaze burner.
 * It is better to use the blockstate to determine the heat source
 * (for performance and synchronization reasons),
 * but it is allowed to check a Tile Entity.
 * Do note that the function is called quite often so don't make it too performance impacting!
 */
public interface IHeatProvider {
    int getHeat(World world, BlockPos pos, IBlockState state);
}
