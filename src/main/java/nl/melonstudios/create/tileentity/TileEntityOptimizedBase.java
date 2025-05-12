package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.tileentity.ISyncedTE;
import com.melonstudios.melonlib.tileentity.TileEntityCachedRenderBB;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import nl.melonstudios.create.util.interfaces.IStateFindable;

public abstract class TileEntityOptimizedBase extends TileEntityCachedRenderBB implements ISyncedTE, ITickable, IStateFindable {
    private int tickRateLazy, tickCounterLazy;
    private boolean syncNextTick = false;
    private boolean requestSyncNextTick = false;
    private boolean initialized = false;
    private boolean unloadedChunk = false;
    private boolean preventNextRemoval = false;
    public void preventNextRemoval() {
        this.preventNextRemoval = true;
    }

    @Override
    public void onChunkUnload() {
        this.unloadedChunk = true;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.unloadedChunk) {
            if (!this.preventNextRemoval) this.remove();
            this.preventNextRemoval = false;
        }
    }

    /**
     * Changes the lazy tick rate for this tile entity.
     * @param rate The new lazy tick rate
     * @since 1.6
     */
    protected void setTickRateLazy(int rate) {
        this.tickRateLazy = this.tickCounterLazy = rate;
    }

    @Override
    public void sync() {
        if (this.pos != null)
            ISyncedTE.super.sync();
        else this.syncNextTick = true;
    }

    @Override
    public void requestSync() {
        if (this.pos != null)
            ISyncedTE.super.requestSync();
        else this.requestSyncNextTick = true;
    }

    @Override
    public void onLoad() {
        if (this.world.isRemote) this.syncNextTick = true;
    }

    @Override
    public boolean compressPacketNBT() {
        return false;
    }

    @Override
    public final void update() {
        if (!this.initialized) {
            this.initialize();
            this.initialized = true;
        }
        if (this.syncNextTick) {
            this.syncNextTick = false;
            this.sync();
        }
        if (this.requestSyncNextTick) {
            this.requestSyncNextTick = false;
            this.requestSync();
        }
        this.tick();
        if (this.tickCounterLazy-- <= this.tickRateLazy) {
            this.tickCounterLazy = this.tickRateLazy;
            this.tickLazy();
        }
    }

    public void destroy() {}

    public void remove() {}

    public abstract void initialize();
    /**
     * Called every tick
     * @since 1.6
     */
    public abstract void tick();

    /**
     * Called every lazy tick
     * @since 1.6
     * @see #setTickRateLazy(int)
     */
    public abstract void tickLazy();

    @Override
    public IBlockState getState() {
        return this.getBlockType().getStateFromMeta(this.getBlockMetadata());
    }
}
