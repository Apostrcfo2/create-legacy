package nl.melonstudios.create.util;

import net.minecraft.util.IStringSerializable;

public enum EnumRenderPart implements IStringSerializable {
    ;

    private final int id = this.ordinal();

    @Override
    public String getName() {
        return this.toString().toLowerCase();
    }
    public int getID() {
        return this.id;
    }

    public static EnumRenderPart byID(int id) {
        return values()[id % values().length];
    }
}
