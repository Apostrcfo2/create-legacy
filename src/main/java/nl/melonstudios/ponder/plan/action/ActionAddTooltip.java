package nl.melonstudios.ponder.plan.action;

import com.melonstudios.melonlib.misc.Localizer;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.scene.PonderTooltip;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionAddTooltip implements IPonderAction {
    private final float x, y, z;
    private final String unlocalizedText;
    private final Object[] format;
    private final long expiresAt;

    public ActionAddTooltip(float x, float y, float z, long expiresAt, String unlocalizedText, Object... format) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.expiresAt = expiresAt;
        this.unlocalizedText = unlocalizedText;
        this.format = format;
    }

    @Override
    public void accept(WorldPonder ponder) {
        ponder.displayedTooltips.add(
                new PonderTooltip(
                        this.x, this.y, this.z,
                        Localizer.translate(this.unlocalizedText, this.format),
                        this.expiresAt
                )
        );
    }
}
