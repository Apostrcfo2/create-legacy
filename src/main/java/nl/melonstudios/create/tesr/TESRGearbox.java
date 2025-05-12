package nl.melonstudios.create.tesr;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tileentity.TileEntityGearbox;

@SideOnly(Side.CLIENT)
public class TESRGearbox<T extends TileEntityGearbox> extends TESRKineticBase<T> {
    @Override //TODO: fix incorrectly rotating shaft halves
    protected void render(T te, float pt, float alpha) {
        final EnumFacing.Axis boxAxis = te.getState().getValue(BlockStateProperties.AXIS);
        final BlockPos pos = te.getPos();

        for (final EnumFacing facing : EnumFacing.VALUES) {
            final EnumFacing.Axis axis = facing.getAxis();
            if (boxAxis == axis) continue;

            float speed = te.getSpeed();
            if (speed != 0 && te.hasSource()) {
                final BlockPos source = te.source.subtract(pos);
                EnumFacing sourceFacing = EnumFacing.getFacingFromVector(source.getX(), source.getY(), source.getZ());
                if (sourceFacing.getAxis() == facing.getAxis()) speed *= sourceFacing == facing ? 1 : -1;
                else if (sourceFacing.getAxisDirection() == facing.getAxisDirection()) speed *= -1;
            }
            this.spinHalfShaft(te, speed, facing, pt);
        }
    }
}
