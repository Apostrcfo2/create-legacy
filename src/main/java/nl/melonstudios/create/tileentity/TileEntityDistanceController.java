package nl.melonstudios.create.tileentity;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.ParametersAreNonnullByDefault;
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

        SubInteractionBox.Interaction interaction = (player, sneaking, held) -> {
            if (!held.isEmpty()) return false;
            this.setDistance = Math.min(MAX_DISTANCE, Math.max(MIN_DISTANCE, this.setDistance + (sneaking ? -1 : 1)));
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

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        nbt.setInteger("dist", this.setDistance);

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        this.setDistance = nbt.getInteger("dist");
    }

    private final ArrayList<SubInteractionBox> subInteractionBoxes = new ArrayList<>();
    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        return this.subInteractionBoxes;
    }
}
