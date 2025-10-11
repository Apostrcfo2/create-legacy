package nl.melonstudios.create.extensions;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IExtensionIBlockState {
    boolean create$isSideSticky(EnumFacing side);
    void create$addStickyLocations(World world, BlockPos pos, EnumFacing side, List<BlockPos> positions);
}
