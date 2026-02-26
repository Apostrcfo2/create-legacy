package nl.melonstudios.create.block.state;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;

public enum EnumDirection implements IStringSerializable {
    LEFT("left", 0, 2, -1, 0),
    UP("up", 1, 3, 0, 1),
    RIGHT("right", 2, 0, 1, 0),
    DOWN("down", 3, 1, 0, -1);

    private final String name;
    private final int id;
    private final int opposite;
    private final int offsetX;
    private final int offsetY;

    EnumDirection(String name, int id, int opposite, int offsetX, int offsetY) {
        this.name = name;
        this.id = id;
        this.opposite = opposite;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public String getName() {
        return this.name;
    }
    public int getId() {
        return this.id;
    }

    public EnumDirection getOpposite() {
        return byId(this.opposite);
    }

    public int getOffsetX() {
        return this.offsetX;
    }
    public int getOffsetY() {
        return this.offsetY;
    }

    private static final EnumDirection[] LOOKUP = {LEFT, UP, RIGHT, DOWN};

    public static EnumDirection byId(int id) {
        return LOOKUP[id];
    }

    public static EnumDirection getRelative(EnumFacing source, EnumFacing other) {
        if (source.getHorizontalIndex() < 0) throw new IllegalArgumentException("Source facing must be horizontal");
        if (other == EnumFacing.UP) return UP;
        if (other == EnumFacing.DOWN) return DOWN;
        if (source.rotateY() == other) return RIGHT;
        if (source.rotateYCCW() == other) return LEFT;
        return DOWN; //fallback
    }

    public static EnumDirection getRelativeMirror(EnumFacing source, EnumFacing other) {
        if (source.getHorizontalIndex() < 0) throw new IllegalArgumentException("Source facing must be horizontal");
        if (other == EnumFacing.UP) return UP;
        if (other == EnumFacing.DOWN) return DOWN;
        if (source.rotateY() == other) return LEFT;
        if (source.rotateYCCW() == other) return RIGHT;
        return DOWN; //fallback
    }

    public EnumFacing getRelative(EnumFacing source) {
        if (source.getHorizontalIndex() < 0) throw new IllegalArgumentException("Source facing must be horizontal");
        if (this == UP) return EnumFacing.UP;
        if (this == DOWN) return EnumFacing.DOWN;
        return this == RIGHT ? source.rotateY() : source.rotateYCCW();
    }
}
