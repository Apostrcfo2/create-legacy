package nl.melonstudios.create.tesr.generator;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tesr.TESRKineticBase;
import nl.melonstudios.create.tileentity.generator.TileEntityCreativeMotor;

@SideOnly(Side.CLIENT)
public class TESRCreativeMotor extends TESRKineticBase<TileEntityCreativeMotor> {
    @Override
    protected void render(TileEntityCreativeMotor te, float pt, float alpha) {
        this.spinHalfShaft(te, te.getSpeed(), te.getRenderFacing(), pt);
    }
}
