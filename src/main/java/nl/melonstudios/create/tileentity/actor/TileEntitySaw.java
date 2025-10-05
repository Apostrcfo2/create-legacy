package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.block.actor.BlockSaw;
import nl.melonstudios.create.tileentity.TileEntityBreakBlockBase;

import java.util.ArrayList;

public class TileEntitySaw extends TileEntityBreakBlockBase {
    @Override
    protected BlockPos getBreakingPos() {
        return this.pos.offset(this.getState().getValue(BlockSaw.FACING).getToEnumFacing());
    }

    @Override
    public boolean canBreak(IBlockState stateToBreak, float blockHardness) {
        return super.canBreak(stateToBreak, blockHardness) && BlockDictionary.isBlockTagged(stateToBreak, "logWood");
    }

    @Override
    public void onBlockBroken(IBlockState stateToBreak) {
        cutDownTree(this.world, this.breakingPos);
    }

    protected float getBreakSpeed() {
        return Math.abs(this.getSpeed()) / 50.0F;
    }

    public EnumFacing facing() {
        return this.getState().getValue(BlockSaw.FACING).getToEnumFacing();
    }

    public static void cutDownTree(World world, BlockPos pos) {
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        for (EnumFacing facing : EnumFacing.HORIZONTALS) {
            mutable.setPos(pos).move(facing);
            if (BlockDictionary.isBlockTagged(world.getBlockState(mutable), "logWood")) {
                world.destroyBlock(pos, true);
                return;
            }
        }
        ArrayList<BlockPos> logs = new ArrayList<>();
        logs.add(pos);
        spreadBranch(world, pos, logs, mutable);

        for (BlockPos log : logs) {
            world.destroyBlock(log, true);
        }
    }

    private static void spreadBranch(World world, BlockPos pos, ArrayList<BlockPos> list, BlockPos.MutableBlockPos mutable) {
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    mutable.setPos(pos.getX() + x, pos.getY() + y, pos.getZ() + z);
                    if (!list.contains(mutable)) {
                        IBlockState state = world.getBlockState(mutable);
                        if (BlockDictionary.isBlockTagged(state, "logWood")) {
                            BlockPos to = mutable.toImmutable();
                            list.add(to);
                            spreadBranch(world, to, list, mutable);
                        }
                    }
                }
            }
        }
    }
}
