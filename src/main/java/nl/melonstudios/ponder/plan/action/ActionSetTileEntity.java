package nl.melonstudios.ponder.plan.action;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.IVirtualizable;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public class ActionSetTileEntity implements IPonderAction {
    private final BlockPos pos;
    private final Supplier<TileEntity> teSupplier;
    private final boolean shouldTick;

    public ActionSetTileEntity(BlockPos pos, Supplier<TileEntity> teSupplier, boolean shouldTick) {
        this.pos = pos;
        this.teSupplier = teSupplier;
        this.shouldTick = shouldTick;
    }

    @Override
    public void accept(WorldPonder ponder) {
        TileEntity te = this.teSupplier.get();
        if (te instanceof IVirtualizable) {
            ((IVirtualizable)te).markAsVirtual();
        }
        te.setWorld(ponder);
        te.setPos(this.pos);
        if (this.shouldTick) {
            ponder.scene.tileEntities.put(this.pos, te);
        } else {
            ponder.scene.nonTickingTileEntities.put(this.pos, te);
        }
    }
}
