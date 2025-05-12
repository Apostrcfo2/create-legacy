package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.util.EnumFacing;

public class TileEntityGearshift extends TileEntitySplitShaftBase {
    @Override
    public float getRotationSpeedModifier(EnumFacing side) {
        if (this.hasSource()) {
            if (side != this.getSourceFacing() && this.getState().getValue(BlockStateProperties.POWERED)) return -1.0F;
        }
        return 1.0F;
    }
}
