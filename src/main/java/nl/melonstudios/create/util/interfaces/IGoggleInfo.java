package nl.melonstudios.create.util.interfaces;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IGoggleInfo {
    List<String> getGoggleInfo(World world, BlockPos pos, IBlockState state);
}
