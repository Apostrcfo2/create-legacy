package nl.melonstudios.create.ponder;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.BlockKineticBase;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

@SideOnly(Side.CLIENT)
public class ActionSetKineticSpeed implements IPonderAction {
    private final BlockPos pos;
    private final float speed;

    public ActionSetKineticSpeed(BlockPos pos, float speed) {
        this.pos = pos;
        this.speed = speed;
    }

    @Override
    public void accept(WorldPonder ponder) {
        TileEntityKinetic te = BlockKineticBase.getKineticTE(ponder, this.pos);
        if (te != null) {
            te.speed = this.speed;
        } else {
            CreateLegacy.logger.warn("Tried to set tile entity speed but TE was missing");
        }
    }
}
