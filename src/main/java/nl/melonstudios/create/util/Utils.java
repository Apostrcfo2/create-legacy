package nl.melonstudios.create.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
public class Utils {
    @Nullable
    public static <T> T cast(Object o, Class<T> clazz) {
        return clazz.isInstance(o) ? (T) o : null;
    }

    public static <T> T axis_choose(EnumFacing.Axis axis, T x, T y, T z) {
        switch (axis) {
            case X: return x;
            case Y: return y;
            case Z: return z;
            default:throw new IllegalStateException("???");
        }
    }
    public static int dist_manh(BlockPos a, BlockPos b) {
        return Math.abs(a.getX() - b.getX()) + Math.abs(a.getY() - b.getY()) + Math.abs(a.getZ() - b.getZ());
    }

    public static final List<EnumFacing> SURROUND_X =
            ImmutableList.of(EnumFacing.UP, EnumFacing.DOWN, EnumFacing.NORTH, EnumFacing.SOUTH);
    public static final List<EnumFacing> SURROUND_Y =
            ImmutableList.of(EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST);
    public static final List<EnumFacing> SURROUND_Z =
            ImmutableList.of(EnumFacing.UP, EnumFacing.DOWN, EnumFacing.EAST, EnumFacing.WEST);
    public static List<EnumFacing> getSurrounding(EnumFacing.Axis axis) {
        switch (axis) {
            case X: return SURROUND_X;
            case Y: return SURROUND_Y;
            case Z: return SURROUND_Z;
            default:return Collections.emptyList();
        }
    }
}
