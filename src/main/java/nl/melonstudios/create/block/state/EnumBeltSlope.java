package nl.melonstudios.create.block.state;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum EnumBeltSlope implements IStringSerializable {
    HORIZONTAL, UPWARD, DOWNWARD, VERTICAL, SIDEWAYS;

    private final String name;
    private final int id;

    EnumBeltSlope() {
        this.name = this.toString().toLowerCase(Locale.ENGLISH);
        this.id = this.ordinal();
    }

    @Override
    public String getName() {
        return this.name;
    }
    public int getId() {
        return this.id;
    }

    public boolean isDiagonal() {
        return this == UPWARD || this == DOWNWARD;
    }
}
