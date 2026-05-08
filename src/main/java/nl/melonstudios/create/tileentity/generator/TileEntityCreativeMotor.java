package nl.melonstudios.create.tileentity.generator;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.tileentity.TileEntityKineticGeneratorBase;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

public class TileEntityCreativeMotor extends TileEntityKineticGeneratorBase {
    private static final float[] SPEEDS = {-256, -128, -64, -32, -16, -8, -4, -2, -1, 1, 2, 4, 8, 16, 32, 64, 128, 256};
    public int speedIndex = 13;

    public TileEntityCreativeMotor() {
        super();
    }

    @Override
    public float getGeneratedSpeed() {
        return SPEEDS[this.speedIndex];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        nbt.setInteger("speedIndex", this.speedIndex);

        return nbt;
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.speedIndex = nbt.getInteger("speedIndex");
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void writePacket(TrackedByteBuf buf) throws IOException {
        super.writePacket(buf);
        buf.writeByte(this.speedIndex);
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void readPacket(ByteBuf buf) throws IOException {
        super.readPacket(buf);
        this.speedIndex = buf.readUnsignedByte();
    }

    @Override
    public void initialize() {
        super.initialize();
        //this.updateGeneratedRotation();
    }

    @SideOnly(Side.CLIENT)
    public EnumFacing getRenderFacing() {
        return this.getState().getValue(BlockStateProperties.FACING);
    }
}
