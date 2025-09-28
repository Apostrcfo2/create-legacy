package nl.melonstudios.create.block.state;

import net.minecraft.util.IStringSerializable;

public enum EnumOrestoneVariant implements IStringSerializable {
    ASURINE("asurine", 0),
    CRIMSITE("crimsite", 1),
    LIMESTONE("limestone", 2),
    OCHRUM("ochrum", 3),
    SCORCHIA("scorchia", 4),
    SCORIA("scoria", 5),
    VERIDIUM("veridium", 6);

    private final String name;
    private final int id;

    EnumOrestoneVariant(String name, int id) {
        this.name = name;
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public static EnumOrestoneVariant byId(int id) {
        return META_LOOKUP[id];
    }

    private static final EnumOrestoneVariant[] META_LOOKUP = {
            ASURINE, CRIMSITE, LIMESTONE, OCHRUM, SCORCHIA, SCORIA, VERIDIUM
    };
}
