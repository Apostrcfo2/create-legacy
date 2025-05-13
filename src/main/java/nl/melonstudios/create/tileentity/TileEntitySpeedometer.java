package nl.melonstudios.create.tileentity;

import nl.melonstudios.create.util.Color;
import nl.melonstudios.create.util.EnumSpeedLevel;
import nl.melonstudios.create.util.Utils;

public class TileEntitySpeedometer extends TileEntityGaugeBase {
    @Override
    public void onSpeedChanged(float lastSpeed) {
        super.onSpeedChanged(lastSpeed);
        float speed = Math.abs(this.getSpeed());

        this.dialTarget = getDialTarget(speed);
        this.color = Color.mixColors(EnumSpeedLevel.of(speed).getColor(), 0xFFFFFF, .25F);

        this.markDirty();
    }

    public static float getDialTarget(float speed) {
        speed = Math.abs(speed);
        float medium = 30.0F;
        float fast = 100.0F;
        float max = 256.0F;
        float target = 0;
        if (speed == 0)
            target = 0;
        else if (speed < medium)
            target = Utils.lerp(speed / medium, 0, .45f);
        else if (speed < fast)
            target =Utils.lerp((speed - medium) / (fast - medium), .45f, .75f);
        else
            target = Utils.lerp((speed - fast) / (max - fast), .75f, 1.125f);
        return target;
    }
}
