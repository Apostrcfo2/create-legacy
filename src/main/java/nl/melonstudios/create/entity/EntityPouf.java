package nl.melonstudios.create.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityPouf extends Entity implements IEntityAdditionalSpawnData {
    public boolean blockBased = false;
    public EntityPouf(World worldIn) {
        super(worldIn);

        this.setSize(0.9F, 0.3F);
    }
    public EntityPouf(World world, BlockPos pos) {
        this(world);
        this.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        this.blockBased = true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.blockBased;
    }

    @Override
    public void setFire(int seconds) {

    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.5F;
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
        if (this.blockBased) return false;
        if (this.canFitPassenger(player) && !this.world.isRemote) {
            return player.startRiding(this);
        }
        return super.processInitialInteract(player, hand);
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setBoolean("blockBased", this.blockBased);
    }
    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        this.blockBased = compound.getBoolean("blockBased");
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeBoolean(this.blockBased);
    }
    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.blockBased = additionalData.readBoolean();
    }
}
