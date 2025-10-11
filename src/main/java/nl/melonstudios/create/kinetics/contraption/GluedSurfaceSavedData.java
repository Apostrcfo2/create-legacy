package nl.melonstudios.create.kinetics.contraption;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.WorldSavedData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashSet;
import java.util.Set;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GluedSurfaceSavedData extends WorldSavedData {
    public final Set<GluedSurface> gluedSurfaces = new HashSet<>();
    public GluedSurfaceSavedData() {
        super("create_glued_surfaces");
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        this.gluedSurfaces.clear();
        NBTTagList list = nbt.getTagList("Surfaces", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            NBTTagCompound compound = list.getCompoundTagAt(i);
            BlockPos pos = new BlockPos(
                    compound.getInteger("x"),
                    compound.getInteger("y"),
                    compound.getInteger("z")
            );
            EnumFacing side = EnumFacing.VALUES[Byte.toUnsignedInt(compound.getByte("side"))];
            this.gluedSurfaces.add(new GluedSurface(pos, side));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (GluedSurface surface : this.gluedSurfaces) {
            NBTTagCompound compound = new NBTTagCompound();
            compound.setInteger("x", surface.pos.getX());
            compound.setInteger("y", surface.pos.getY());
            compound.setInteger("z", surface.pos.getZ());
            compound.setByte("side", (byte)surface.side.getIndex());
            list.appendTag(compound);
        }
        nbt.setTag("Surfaces", list);
        return nbt;
    }
}
