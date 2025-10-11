package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.extensions.IExtensionIBlockState;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class StickinessPropagator {
    public static void propagateStickiness(World world, BlockPos pos, int maximum, List<BlockPos> positions, AtomicBoolean failed) {
        if (positions.size() > maximum) {
            failed.set(true);
            positions.clear();
        }
        if (failed.get()) return;
        IBlockState state = world.getBlockState(pos);
        for (EnumFacing side : EnumFacing.VALUES) {
            ((IExtensionIBlockState)state).create$addStickyLocations(world, pos, side, positions);
        }
    }
}
