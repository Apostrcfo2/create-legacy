package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.util.interfaces.ICogwheel;

public class TileEntityCogwheel extends TileEntityKinetic {
    public boolean isLarge() {
        return ((ICogwheel)this.blockType).isLargeCog();
    }

    @SideOnly(Side.CLIENT)
    public EnumFacing.Axis getRenderAxis() {
        return this.getState().getValue(BlockStateProperties.AXIS);
    }
}
