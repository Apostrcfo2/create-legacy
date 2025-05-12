package nl.melonstudios.create.tesr;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tileentity.TileEntitySplitShaftBase;

@SideOnly(Side.CLIENT)
public class TESRSplitShaft<T extends TileEntitySplitShaftBase> extends TESRKineticBase<T> {
    @Override
    protected void render(TileEntitySplitShaftBase te, float pt, float alpha) {
        float speed = te.getSpeed();
        {
            EnumFacing positive = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, te.getRenderAxis());
            float adjusted = speed * te.getRotationSpeedModifier(positive);
            this.spinHalfShaft(te.getWorld(), adjusted, positive, pt);
        }
        {
            EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, te.getRenderAxis());
            float adjusted = speed * te.getRotationSpeedModifier(negative);
            this.spinHalfShaft(te.getWorld(), adjusted, negative, pt);
        }
    }
}
