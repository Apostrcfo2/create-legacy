package nl.melonstudios.ponder.plan.action;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionSetBlock implements IPonderAction {
    private final BlockPos pos;
    private final IBlockState state;

    public ActionSetBlock(BlockPos pos, IBlockState state) {
        this.pos = pos;
        this.state = state;
    }

    @Override
    public void accept(WorldPonder worldPonder) {
        worldPonder.setBlockState(this.pos, this.state);
    }

    @Override
    public boolean requiresMeshUpdate() {
        return true;
    }
}
