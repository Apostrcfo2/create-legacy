package nl.melonstudios.create.ponder;

import net.minecraft.tileentity.TileEntity;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Predicate;

public class ActionSetKineticSpeedEnMasse implements IPonderAction {
    private final float speed;
    private final Predicate<TileEntityKinetic> filter;

    public ActionSetKineticSpeedEnMasse(float speed, Predicate<TileEntityKinetic> filter) {
        this.speed = speed;
        this.filter = filter;
    }

    @Override
    public void accept(WorldPonder ponder) {
        for (TileEntity te : ponder.scene.tileEntities.values()) {
            if (te instanceof TileEntityKinetic) {
                TileEntityKinetic kinetic = (TileEntityKinetic) te;
                if (this.filter.test(kinetic)) kinetic.speed = this.speed;
            }
        }
        for (TileEntity te : ponder.scene.nonTickingTileEntities.values()) {
            if (te instanceof TileEntityKinetic) {
                TileEntityKinetic kinetic = (TileEntityKinetic) te;
                if (this.filter.test(kinetic)) kinetic.speed = this.speed;
            }
        }
    }
}
