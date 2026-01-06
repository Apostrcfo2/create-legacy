package nl.melonstudios.ponder.plan.action;

import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionSetOffset implements IPonderAction {
    private final int offsetX, offsetY, offsetZ;

    public ActionSetOffset(int x, int y, int z) {
        this.offsetX = x;
        this.offsetY = y;
        this.offsetZ = z;
    }

    @Override
    public void accept(WorldPonder ponder) {
        ponder.offsetX = this.offsetX;
        ponder.offsetY = this.offsetY;
        ponder.offsetZ = this.offsetZ;
    }
}
