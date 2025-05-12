package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntitySplitShaftBase extends TileEntityDirectionalShaftHalves {
    public abstract float getRotationSpeedModifier(EnumFacing side);

    @SideOnly(Side.CLIENT)
    public EnumFacing.Axis getRenderAxis() {
        return this.getState().getValue(BlockStateProperties.AXIS);
    }
}
