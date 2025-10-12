package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.extensions.IExtensionBlock;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class StickinessPropagator {
    public static void propagateStickiness(
            World world, BlockPos pos, int maximum,
            Set<BlockPos> positions, Set<EntityGlue> glues, AtomicBoolean failed
    ) {
        if (failed.get()) return;
        if (positions.contains(pos)) return;
        if (positions.size() > maximum) {
            positions.clear();
            failed.set(true);
            return;
        }
        IBlockState state = world.getBlockState(pos);
        if (state.getMobilityFlag() == EnumPushReaction.BLOCK) {
            positions.clear();
            failed.set(true);
            return;
        }
        if (state.getBlock().isAir(state, world, pos)) return;
        positions.add(pos);
        List<BlockPos> list = new ArrayList<>();
        ((IExtensionBlock)state.getBlock()).create$addStickyLocations(world, pos, state, list);
        for (BlockPos sticky : list) {
            propagateStickiness(
                    world, sticky, maximum,
                    positions, glues, failed
            );
        }
        for (EnumFacing side : EnumFacing.VALUES) {
            GluedSurface surface = new GluedSurface(pos, side);
            List<EntityGlue> glue = world.getEntities(EntityGlue.class, (e) -> surface.equals(e.getSurface()));
            if (!glue.isEmpty()) {
                glues.addAll(glue);
                propagateStickiness(
                        world, pos.offset(side), maximum,
                        positions, glues, failed
                );
            }
        }
    }
}
