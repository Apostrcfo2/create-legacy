package nl.melonstudios.create.tileentity.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockDrill;
import nl.melonstudios.create.tileentity.TileEntityBreakBlockBase;

public class TileEntityDrill extends TileEntityBreakBlockBase {
    private IBlockState currentOutput;

    public TileEntityDrill() {
        this.currentOutput = Blocks.AIR.getDefaultState();
    }

    @Override
    protected BlockPos getBreakingPos() {
        return this.pos.offset(this.getState().getValue(BlockDrill.FACING));
    }
}
