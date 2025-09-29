package nl.melonstudios.create.block.state;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumSawRotation implements IStringSerializable {
    NORTH("north", 0, EnumFacing.NORTH),
    EAST("east", 1, EnumFacing.EAST),
    SOUTH("south", 2, EnumFacing.SOUTH),
    WEST("west", 3, EnumFacing.WEST),
    UP_ALONG_X("up_along_x", 4, EnumFacing.UP),
    UP_ALONG_Z("up_along_z", 5, EnumFacing.UP),
    DOWN_ALONG_X("down_along_x", 6, EnumFacing.DOWN),
    DOWN_ALONG_Z("down_along_z", 7, EnumFacing.DOWN);

    private final String name;
    private final int meta;
    private final EnumFacing toEnumFacing;
    EnumSawRotation(String name, int meta, EnumFacing facing) {
        this.name = name;
        this.meta = meta;
        this.toEnumFacing = facing;
    }

    @Override
    public String getName() {
        return this.name;
    }
    public int getMeta() {
        return this.meta;
    }
    public EnumFacing getToEnumFacing() {
        return this.toEnumFacing;
    }

    private static final EnumSawRotation[] META_LOOKUP = {
            NORTH, EAST, SOUTH, WEST,
            UP_ALONG_X, UP_ALONG_Z,
            DOWN_ALONG_X, DOWN_ALONG_Z
    };
    public static EnumSawRotation byMeta(int meta) {
        return META_LOOKUP[meta];
    }

    public static EnumSawRotation findSneakStateWithContext(EnumFacing placedOn, EnumFacing placerFacing) {
        if (placedOn.getAxis() == EnumFacing.Axis.Y) {
            return placerFacing.getAxis() == EnumFacing.Axis.X ? UP_ALONG_X : UP_ALONG_Z;
        }
        switch (placedOn) {
            case NORTH:return SOUTH;
            case EAST: return WEST;
            case SOUTH:return NORTH;
            case WEST: return EAST;
            default:   return UP_ALONG_X;
        }
    }
}
