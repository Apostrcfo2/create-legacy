package nl.melonstudios.create.kinetics.contraption;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.material.Material;
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
        if (state.getMaterial().isLiquid() || state.getBlock() instanceof BlockLiquid) return;
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
        boolean sticksToSelf = BlockDictionary.isBlockTagged(state, "create:sticksToSelf");
        for (EnumFacing side : EnumFacing.VALUES) {
            GluedSurface surface = new GluedSurface(pos, side);
            List<EntityGlue> glue = world.getEntities(EntityGlue.class, (e) -> surface.equals(e.getSurface()));
            BlockPos off = pos.offset(side);
            if (!glue.isEmpty()) {
                glues.addAll(glue);
                propagateStickiness(
                        world, off, maximum,
                        positions, glues, failed
                );
            } else if (sticksToSelf) {
                IBlockState hi = world.getBlockState(off);
                if (state == hi) {
                    propagateStickiness(
                            world, off, maximum,
                            positions, glues, failed
                    );
                }
            }
        }
    }
}
