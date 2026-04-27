package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

public abstract class TileEntityGaugeBase extends TileEntityKinetic {
    public float dialTarget;
    public float dialState;
    public float prevDialState;
    public int color;

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setFloat("dialValue", this.dialTarget);
        compound.setInteger("dialColor", this.color);

        return compound;
    }
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.dialTarget = compound.getFloat("dialValue");
        this.color = compound.getInteger("dialColor");
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void writePacket(TrackedByteBuf buf) throws IOException {
        super.writePacket(buf);

        buf.writeFloat(this.dialTarget);
        buf.writeInt(this.color);
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void readPacket(ByteBuf buf) throws IOException {
        super.readPacket(buf);

        this.dialTarget = buf.readFloat();
        this.color = buf.readInt();
    }

    @Override
    public void tick() {
        super.tick();
        this.prevDialState = this.dialState;
        this.dialState += (this.dialTarget - this.dialState) * 0.125F;
        if (this.dialState > 1 && this.world.rand.nextFloat() < 0.5F)
            this.dialState -= (this.dialState - 1) * this.world.rand.nextFloat();
    }
}
