package nl.melonstudios.create.block.state;

import net.minecraft.util.IStringSerializable;

public enum EnumBeltPart implements IStringSerializable {
    START("start", 0b00),
    END("end", 0b01),
    MIDDLE("middle", 0b10),
    PULLEY("pulley", 0b11);

    private final String name;
    private final int id;
    EnumBeltPart(String name, int id) {
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

    private static final EnumBeltPart[] VARIANTS = new EnumBeltPart[values().length];
    public static EnumBeltPart byId(int id) {
        return VARIANTS[id & 3];
    }

    static {
        for (EnumBeltPart part : values()) {
            VARIANTS[part.getId()] = part;
        }
    }
}
