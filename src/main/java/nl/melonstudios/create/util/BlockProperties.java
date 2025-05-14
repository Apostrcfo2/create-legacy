package nl.melonstudios.create.util;

import com.melonstudios.melonlib.misc.AABB;
import net.minecraft.util.math.AxisAlignedBB;

public class BlockProperties {
    public static final float WOOD_HARDNESS = 2.0F;
    public static final float WOOD_RESISTANCE = 2.0F;

    public static final float STONE_HARDNESS = 1.5F;
    public static final float STONE_RESISTANCE = 6.0F;

    public static final float ORE_HARDNESS = 3.0F;
    public static final float ORE_RESISTANCE = 1.5F;

    public static final float IRON_HARDNESS = 5.0F;
    public static final float IRON_RESISTANCE = 6.0F;

    public static final AxisAlignedBB SHAFT_X_AABB = AABB.create(0, 5, 5, 16, 11, 11);
    public static final AxisAlignedBB SHAFT_Y_AABB = AABB.create(5, 0, 5, 11, 16, 11);
    public static final AxisAlignedBB SHAFT_Z_AABB = AABB.create(5, 5, 0, 11, 11, 16);

    public static final AxisAlignedBB GEAR_X_AABB = AABB.create(0, 2, 2, 16, 14, 14);
    public static final AxisAlignedBB GEAR_Y_AABB = AABB.create(2, 0, 2, 14, 16, 14);
    public static final AxisAlignedBB GEAR_Z_AABB = AABB.create(2, 2, 0, 14, 14, 16);

    public static final AxisAlignedBB CASING_12PX_UP = AABB.create(0, 0, 0, 16, 12, 16);
    public static final AxisAlignedBB CASING_12PX_DOWN = AABB.create(0, 4, 0, 16, 16, 16);
    public static final AxisAlignedBB CASING_12PX_NORTH = AABB.create(0, 0, 4, 16, 16, 16);
    public static final AxisAlignedBB CASING_12PX_SOUTH = AABB.create(0, 0, 0, 16, 16, 12);
    public static final AxisAlignedBB CASING_12PX_WEST = AABB.create(4, 0, 0, 16, 16, 16);
    public static final AxisAlignedBB CASING_12PX_EAST = AABB.create(0, 0, 0, 12, 16, 16);

    public static final AxisAlignedBB[] CASING_12PX_MAPPED = {
            CASING_12PX_DOWN,
            CASING_12PX_UP,
            CASING_12PX_NORTH,
            CASING_12PX_SOUTH,
            CASING_12PX_WEST,
            CASING_12PX_EAST
    };
}
