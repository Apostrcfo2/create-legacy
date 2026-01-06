package nl.melonstudios.ponder.plan.action;

import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionSetScale implements IPonderAction {
    private final float scale;

    public ActionSetScale(float scale) {
        this.scale = scale;
    }

    @Override
    public void accept(WorldPonder ponder) {
        ponder.scale = this.scale;
    }
}
