package nl.melonstudios.create.tileentity;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class TileEntityDirectionalShaftHalves extends TileEntityKinetic {
    public EnumFacing getSourceFacing() {
        BlockPos localSource = this.source.subtract(this.getPos());
        return EnumFacing.getFacingFromVector(localSource.getX(), localSource.getY(), localSource.getZ());
    }
}
