package nl.melonstudios.ponder.plan.action;

import net.minecraft.util.math.BlockPos;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionRemoveTileEntity implements IPonderAction {
    private final BlockPos pos;

    public ActionRemoveTileEntity(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void accept(WorldPonder ponder) {
        ponder.scene.tileEntities.remove(this.pos);
        ponder.scene.nonTickingTileEntities.remove(this.pos);
    }
}
