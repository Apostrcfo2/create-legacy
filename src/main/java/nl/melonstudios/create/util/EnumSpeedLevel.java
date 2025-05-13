package nl.melonstudios.create.util;

import net.minecraft.util.text.TextFormatting;

public enum EnumSpeedLevel {
    NONE(TextFormatting.DARK_GRAY, 0x000000, 0),
    SLOW(TextFormatting.GREEN, 0x22FF22, 10),
    MEDIUM(TextFormatting.AQUA, 0x0084FF, 20),
    FAST(TextFormatting.LIGHT_PURPLE, 0xFF55FF, 30);

    private final TextFormatting textColor;
    private final int color;
    private final int particleSpeed;

    EnumSpeedLevel(TextFormatting textColor, int color, int particleSpeed) {
        this.textColor = textColor;
        this.color = color;
        this.particleSpeed = particleSpeed;
    }

    public TextFormatting getTextColor() {
        return textColor;
    }
    public int getColor() {
        return color;
    }
    public int getParticleSpeed() {
        return particleSpeed;
    }

    public float getSpeedValue() {
        switch (this) {
            case FAST: return 100.0F;
            case MEDIUM: return 30.0F;
            case SLOW: return 1.0F;
            default: return 0.0F;
        }
    }

    public static EnumSpeedLevel of(float speed) {
        speed = Math.abs(speed);

        if (speed >= 100.0F) return FAST;
        if (speed >= 30.0F) return MEDIUM;
        if (speed >= 1.0F) return SLOW;
        return NONE;
    }
}
