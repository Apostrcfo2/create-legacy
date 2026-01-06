package nl.melonstudios.ponder.plan.action;

import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionSetRotation implements IPonderAction {
    private final float yaw, pitch;

    public ActionSetRotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void accept(WorldPonder ponder) {
        if (!Float.isNaN(this.yaw)) ponder.yaw = this.yaw;
        if (!Float.isNaN(this.pitch)) ponder.pitch = this.pitch;
    }
}
