package nl.melonstudios.create.tileentity;

import net.minecraft.nbt.NBTTagCompound;

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

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        nbt.setFloat("value", this.dialTarget);
        nbt.setInteger("color", this.color);

        return nbt;
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        this.dialTarget = nbt.getFloat("value");
        this.color = nbt.getInteger("color");
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
