package nl.melonstudios.ponder.plan.action;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Consumer;

@SuppressWarnings("unchecked")
public class ActionModifyTileEntity<T extends TileEntity> implements IPonderAction {
    private final BlockPos pos;
    private final Class<T> clazz;
    private final Consumer<T> action;

    public ActionModifyTileEntity(BlockPos pos, Class<T> clazz, Consumer<T> action) {
        this.pos = pos;
        this.clazz = clazz;
        this.action = action;
    }

    @Override
    public void accept(WorldPonder ponder) {
        TileEntity te = ponder.scene.tileEntities.get(this.pos);
        if (this.clazz.isInstance(te)) this.action.accept((T)te);
        te = ponder.scene.nonTickingTileEntities.get(this.pos);
        if (this.clazz.isInstance(te)) this.action.accept((T)te);
    }
}
