package nl.melonstudios.ponder.scene;

public class PonderTooltip {
    public final float x, y, z;
    public final String text;
    public final long expiresAt;

    public PonderTooltip(float x, float y, float z, String text, long expiresAt) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.text = text;
        this.expiresAt = expiresAt;
    }
}
