package nl.melonstudios.create.util;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.BlockLog;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import nl.melonstudios.create.util.interfaces.ISail;
import org.lwjgl.util.vector.Vector3f;

import java.util.Collection;

public class BlockRotationHelper {
    public static final float RADIANS = 0.017453294F;
    private BlockRotationHelper() {
        throw new AssertionError("nuh uh");
    }

    public static EnumFacing rotate(EnumFacing facing, EnumFacing.Axis axis, Rotation rotation) {
        switch (rotation) {
            case NONE:
                return facing;
            case CLOCKWISE_90:
                return facing.rotateAround(axis);
            case CLOCKWISE_180:
                return facing.rotateAround(axis).rotateAround(axis);
            case COUNTERCLOCKWISE_90:
                return facing.rotateAround(axis).rotateAround(axis).rotateAround(axis);
            default:
                throw new IllegalStateException("Unexpected value: " + rotation);
        }
    }
    public static BlockPos transform(BlockPos anchor, EnumFacing.Axis axis, Rotation rotation, BlockPos local) {
        if (rotation == Rotation.NONE) return anchor.add(local);
        switch (axis) {
            case Y:
                switch (rotation) {
                    case CLOCKWISE_90:
                        return anchor.add(-local.getZ(), local.getY(), local.getX());
                    case CLOCKWISE_180:
                        return anchor.add(-local.getX(), local.getY(), -local.getZ());
                    case COUNTERCLOCKWISE_90:
                        return anchor.add(local.getZ(), local.getY(), -local.getX());
                }
                break;
            case X:
                switch (rotation) {
                    case CLOCKWISE_90:
                        return anchor.add(local.getX(), local.getZ(), -local.getY());
                    case CLOCKWISE_180:
                        return anchor.add(local.getX(), -local.getY(), -local.getZ());
                    case COUNTERCLOCKWISE_90:
                        return anchor.add(local.getX(), -local.getZ(), local.getY());
                }
                break;
            case Z:
                switch (rotation) {
                    case CLOCKWISE_90:
                        return anchor.add(local.getY(), -local.getX(), local.getZ());
                    case CLOCKWISE_180:
                        return anchor.add(-local.getX(), -local.getY(), local.getZ());
                    case COUNTERCLOCKWISE_90:
                        return anchor.add(-local.getY(), local.getX(), local.getZ());
                }
                break;
        }
        return anchor.add(local);
    }
    public static IBlockState rotate(IBlockState state, EnumFacing.Axis axis, Rotation rotation) {
        if (rotation == Rotation.NONE) return state; //easy way out
        if (axis == EnumFacing.Axis.Y) {
            return state.withRotation(rotation); //built in functioning
        }
        Collection<IProperty<?>> props = state.getPropertyKeys();
        if (props.contains(BlockStateProperties.FACING)) {
            EnumFacing facing = state.getValue(BlockStateProperties.FACING);
            return state.withProperty(BlockStateProperties.FACING, rotate(facing, axis, rotation));
        }
        if (props.contains(BlockStateProperties.AXIS)) {
            EnumFacing.Axis axis1 = state.getValue(BlockStateProperties.AXIS);
            if (axis1 == axis || rotation == Rotation.CLOCKWISE_180) return state;
            return state.withProperty(BlockStateProperties.AXIS,
                    EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis1).rotateAround(axis)
                            .getAxis());
        }
        if (state.getBlock() instanceof ISail) {
            ISail sail = (ISail) state.getBlock();
            return sail.withFacing(state, rotate(sail.getFacing(state), axis, rotation));
        }
        if (props.contains(BlockLog.LOG_AXIS)) {
            if (rotation == Rotation.CLOCKWISE_180) return state;
            BlockLog.EnumAxis axis1 = state.getValue(BlockLog.LOG_AXIS);
            if (axis1 == BlockLog.EnumAxis.fromFacingAxis(axis)) return state;
            EnumFacing.Axis axis2 = EnumFacing.Axis.byName(axis1.getName());
            if (axis2 == null) return state;
            return state.withProperty(BlockLog.LOG_AXIS,
                    BlockLog.EnumAxis.fromFacingAxis(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis2).rotateAround(axis)
                            .getAxis()));
        }
        if (rotation == Rotation.CLOCKWISE_180) {
            return state.withMirror(axis == EnumFacing.Axis.X ? Mirror.LEFT_RIGHT : Mirror.FRONT_BACK);
        }
        return state;
    }

    public static Rotation getRotationForAngle(float angle) {
        angle %= 360;
        if (angle < 0) angle += 360;
        if (angle < 45) return Rotation.NONE;
        if (angle < 45+90) return Rotation.COUNTERCLOCKWISE_90;
        if (angle < 45+180) return Rotation.CLOCKWISE_180;
        if (angle < 45+270) return Rotation.CLOCKWISE_90;
        return Rotation.NONE;
    }

    public static void rotateNormal(Vec3i vec, EnumFacing.Axis axis, float angle, Vector3f store) {
        if (angle == 0.0F) {
            store.set(vec.getX(), vec.getY(), vec.getZ());
            return;
        }
        angle *= RADIANS;
        float x, y, z;
        switch (axis) {
            case X:
                y = vec.getY();
                z = vec.getZ();
                store.set(vec.getX(), y*MathHelper.cos(angle)-z*MathHelper.sin(angle), y*MathHelper.sin(angle)+z*MathHelper.cos(angle));
                break;
            case Y:
                x = vec.getX();
                z = vec.getZ();
                store.set(x*MathHelper.cos(-angle)-z*MathHelper.sin(-angle), vec.getY(), x*MathHelper.sin(-angle)+z*MathHelper.cos(-angle));
                break;
            case Z:
                x = vec.getX();
                y = vec.getY();
                store.set(x*MathHelper.cos(angle)-y*MathHelper.sin(angle), x*MathHelper.sin(angle)+y*MathHelper.cos(angle), vec.getZ());
                break;
            default:
                store.set(vec.getX(), vec.getY(), vec.getZ());
        }
    }

    public static void rotateNormal(Vector3f vec, EnumFacing.Axis axis, float angle, Vector3f store) {
        if (angle == 0.0F) {
            store.set(vec.getX(), vec.getY(), vec.getZ());
            return;
        }
        angle *= RADIANS;
        float x, y, z;
        switch (axis) {
            case X:
                y = vec.getY();
                z = vec.getZ();
                store.set(vec.getX(), y*MathHelper.cos(angle)-z*MathHelper.sin(angle), y*MathHelper.sin(angle)+z*MathHelper.cos(angle));
                break;
            case Y:
                x = vec.getX();
                z = vec.getZ();
                store.set(x*MathHelper.cos(-angle)-z*MathHelper.sin(-angle), vec.getY(), x*MathHelper.sin(-angle)+z*MathHelper.cos(-angle));
                break;
            case Z:
                x = vec.getX();
                y = vec.getY();
                store.set(x*MathHelper.cos(angle)-y*MathHelper.sin(angle), x*MathHelper.sin(angle)+y*MathHelper.cos(angle), vec.getZ());
                break;
            default:
                store.set(vec.getX(), vec.getY(), vec.getZ());
        }
    }
}
