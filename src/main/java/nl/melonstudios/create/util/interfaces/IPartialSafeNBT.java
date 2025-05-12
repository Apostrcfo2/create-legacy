package nl.melonstudios.create.util.interfaces;

import net.minecraft.nbt.NBTTagCompound;

public interface IPartialSafeNBT {
    void writeSafe(NBTTagCompound nbt);
}
