package nl.melonstudios.create.block.state;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum EnumBeltPart implements IStringSerializable {
    START, MIDDLE, END, PULLEY;

    private final String name;
    private final int id;
    EnumBeltPart() {
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
}
