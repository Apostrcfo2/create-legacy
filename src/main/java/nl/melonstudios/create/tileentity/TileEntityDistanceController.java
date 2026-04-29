package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityDistanceController extends TileEntityOptimizedBase implements ITileEntityWithSubInteractions {
    public static final int MIN_DISTANCE = 3;
    public static final int MAX_DISTANCE = 32;
    public int setDistance = 8;

    public TileEntityDistanceController() {
        super();

        SubInteractionBox.ScrollInteraction interaction = (player, sneaking, held, direction) -> {
            if (held.isEmpty() || held.getItem() != ItemInit.WRENCH) return false;
            if (sneaking) return false;
            this.setDistance = Math.min(MAX_DISTANCE, Math.max(MIN_DISTANCE, this.setDistance + (int)Math.signum(direction)));
            this.sync();
            return true;
        };
        for (EnumFacing side : EnumFacing.VALUES) {
            this.subInteractionBoxes.add(SubInteractionBox.Helper.createCenteredSide(side, 0.25F, interaction));
        }
    }

    @Override
    public void tick() {

    }

    @Override
    public void tickLazy() {

    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("setDistance", this.setDistance);
        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.setDistance = nbt.getInteger("setDistance");
        super.readFromNBT(nbt);
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void writePacket(TrackedByteBuf buf) throws IOException {
        buf.writeByte(this.setDistance);
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void readPacket(ByteBuf buf) throws IOException {
        this.setDistance = buf.readUnsignedByte();
    }

    private final ArrayList<SubInteractionBox> subInteractionBoxes = new ArrayList<>();
    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        return this.subInteractionBoxes;
    }
}
