package nl.melonstudios.create.tileentity.actor;

import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockDrill;
import nl.melonstudios.create.tileentity.TileEntityBreakBlockBase;

public class TileEntityDrill extends TileEntityBreakBlockBase {
    public TileEntityDrill() {
        this.setTickRateLazy(10);}

    @Override
    protected BlockPos getBreakingPos() {
        return this.pos.offset(this.getState().getValue(BlockDrill.FACING));
    }
}
