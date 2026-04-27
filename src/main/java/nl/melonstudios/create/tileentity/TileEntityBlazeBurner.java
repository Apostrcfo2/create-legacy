package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import nl.melonstudios.create.block.BlockBlazeBurner;
import nl.melonstudios.create.util.Utils;

public class TileEntityBlazeBurner extends TileEntityOptimizedBase {
    public int fuelTicks = 0; //Above 8000 is superheated

    public TileEntityBlazeBurner() {

    }

    @Override
    public void tick() {
        if (this.fuelTicks > 0 && !this.isVirtual()) {
            this.fuelTicks--;
            this.markDirty();
            if (!this.world.isRemote) {
                IBlockState oldState = this.getState();
                IBlockState newState = oldState.withProperty(BlockBlazeBurner.VARIANT, this.fuelTicks > 8000 ? BlockBlazeBurner.Variant.SUPERHEATED :
                        this.fuelTicks != 0 ? BlockBlazeBurner.Variant.HEATED : BlockBlazeBurner.Variant.PASSIVE);
                if (oldState != newState) {
                    Utils.setBlockTESafe(this.world, this.pos, newState, 3);
                    this.syncNextTick();
                }
            }
        }
    }

    @Override
    public void tickLazy() {

    }

    public void feed(int ticks) {
        this.fuelTicks = Math.min(this.fuelTicks + ticks, 8000);
        this.sync();
    }
    public void blazecake(int ticks) {
        this.fuelTicks = 8000 + ticks;
        this.sync();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("fuelTicks", this.fuelTicks);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.fuelTicks = nbt.getInteger("fuelTicks");
    }

    @Override
    public void writePacket(TrackedByteBuf buf) {
        buf.writeShort(this.fuelTicks);
    }

    @Override
    public void readPacket(ByteBuf buf) {
        this.fuelTicks = buf.readUnsignedShort();
    }
}
