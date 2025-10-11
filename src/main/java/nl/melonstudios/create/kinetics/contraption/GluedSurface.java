package nl.melonstudios.create.kinetics.contraption;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;

public class GluedSurface {
    public final BlockPos pos;
    public final EnumFacing side;

    public GluedSurface(BlockPos pos, EnumFacing side) {
        if (side.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE) {
            this.pos = pos;
            this.side = side;
        } else {
            this.pos = pos.offset(side);
            this.side = side.getOpposite();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GluedSurface)) return false;
        GluedSurface that = (GluedSurface) o;
        return Objects.equals(pos, that.pos) && side == that.side;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.pos.toLong()) ^ (this.side.getIndex() << 3);
    }
}
