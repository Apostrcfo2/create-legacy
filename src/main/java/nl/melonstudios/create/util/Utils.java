package nl.melonstudios.create.util;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.MapStorage;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("all")
public class Utils {
    @Nullable
    @SuppressWarnings("unchecked")
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

    public static int lerpInt(float delta, int start, int end) {
        return start + floor(delta * (float)(end - start));
    }

    public static float lerp(float delta, float start, float end) {
        return start + delta * (end - start);
    }

    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }

    public static double lerp2(double delta1, double delta2, double start1, double end1, double start2, double end2) {
        return lerp(delta2, lerp(delta1, start1, end1), lerp(delta1, start2, end2));
    }

    public static double lerp3(double delta1, double delta2, double delta3, double start1, double end1, double start2, double end2, double start3, double end3, double start4, double end4) {
        return lerp(delta3, lerp2(delta1, delta2, start1, end1, start2, end2), lerp2(delta1, delta2, start3, end3, start4, end4));
    }

    public static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    public static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }

    public static long lfloor(double value) {
        long i = (long)value;
        return value < (double)i ? i - 1L : i;
    }

    public static Vec3d rotate(Vec3d vec, double deg, EnumFacing.Axis axis) {
        if (deg == 0)
            return vec;
        if (vec == Vec3d.ZERO)
            return vec;

        float angle = (float) (deg / 180f * Math.PI);
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        double x = vec.x;
        double y = vec.y;
        double z = vec.z;

        if (axis == EnumFacing.Axis.X)
            return new Vec3d(x, y * cos - z * sin, z * cos + y * sin);
        if (axis == EnumFacing.Axis.Y)
            return new Vec3d(x * cos + z * sin, y, z * cos - x * sin);
        if (axis == EnumFacing.Axis.Z)
            return new Vec3d(x * cos - y * sin, y * cos + x * sin, z);
        return vec;
    }
}
