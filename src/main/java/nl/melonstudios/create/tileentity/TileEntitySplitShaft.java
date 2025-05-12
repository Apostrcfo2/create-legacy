package nl.melonstudios.create.tileentity;

import net.minecraft.util.EnumFacing;

public abstract class TileEntitySplitShaft extends TileEntityDirectionalShaftHalves {
    public abstract float getRotationSpeedModifier(EnumFacing side);
}
