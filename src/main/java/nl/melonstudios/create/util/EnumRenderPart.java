package nl.melonstudios.create.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

import java.util.HashMap;
import java.util.Objects;

public enum EnumRenderPart implements IStringSerializable {
    SAW_X,
    SAW_Y,
    SAW_Z,

    HALF_SHAFT_UP,
    HALF_SHAFT_DOWN,
    HALF_SHAFT_NORTH,
    HALF_SHAFT_SOUTH,
    HALF_SHAFT_EAST,
    HALF_SHAFT_WEST,
    ;

    private final int id = this.ordinal();
    private final String name = this.toString().toLowerCase();

    @Override
    public String getName() {
        return this.name;
    }
    public int getID() {
        return this.id;
    }

    private static final HashMap<String, EnumRenderPart> NAME_TO_ENUM = new HashMap<>();

    private static final EnumRenderPart[] SAWS = new EnumRenderPart[3];
    private static final EnumRenderPart[] HALF_SHAFTS = new EnumRenderPart[6];

    public static EnumRenderPart byID(int id) {
        return values()[id % values().length];
    }
    public static EnumRenderPart byName(String name) {
        return Objects.requireNonNull(NAME_TO_ENUM.get(name), "No such enum with name " + name);
    }

    static {
        for (EnumRenderPart erp : EnumRenderPart.values()) {
            NAME_TO_ENUM.put(erp.name, erp);
        }

        SAWS[0] = SAW_X;
        SAWS[1] = SAW_Y;
        SAWS[2] = SAW_Z;

        HALF_SHAFTS[0] = HALF_SHAFT_UP;
        HALF_SHAFTS[1] = HALF_SHAFT_DOWN;
        HALF_SHAFTS[2] = HALF_SHAFT_NORTH;
        HALF_SHAFTS[3] = HALF_SHAFT_SOUTH;
        HALF_SHAFTS[4] = HALF_SHAFT_EAST;
        HALF_SHAFTS[5] = HALF_SHAFT_WEST;
    }

    public static EnumRenderPart getSaw(EnumFacing.Axis axis) {
        return SAWS[axis.ordinal()];
    }
    public static EnumRenderPart getHalfShaft(EnumFacing facing) {
        return HALF_SHAFTS[facing.getIndex()];
    }
}
