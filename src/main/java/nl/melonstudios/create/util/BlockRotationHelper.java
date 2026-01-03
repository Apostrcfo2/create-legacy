package nl.melonstudios.create.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;

public class BlockRotationHelper {
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
}
