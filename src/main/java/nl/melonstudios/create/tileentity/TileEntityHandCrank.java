package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.block.BlockHandCrank;

public class TileEntityHandCrank extends TileEntityKineticGenerator {
    public int inUse;
    public boolean backwards;

    public void turn(boolean back) {
        boolean update = this.getGeneratedSpeed() == 0 || back != this.backwards;

        this.inUse = 10;
        this.backwards = back;
        if (update && !this.world.isRemote) {
            this.updateGeneratedRotation();
        }
    }

    @Override
    public float getGeneratedSpeed() {
        final Block block = this.getBlockType();
        if (!(block instanceof BlockHandCrank)) return 0.0F;
        int speed = (this.inUse == 0 ? 0 : this.backwards ? -1 : 1) * ((BlockHandCrank)block).getRotationSpeed();
        return convertToDirection(speed, this.getState().getValue(BlockHandCrank.FACING));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);

        compound.setInteger("inUse", this.inUse);
        compound.setBoolean("backwards", this.backwards);

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.inUse = compound.getInteger("inUse");
        this.backwards = compound.getBoolean("backwards");
    }

    @Override
    public void tick() {
        super.tick();

        if (this.inUse > 0) {
            this.inUse--;

            if (this.inUse == 0 && !this.world.isRemote) {
                this.updateGeneratedRotation();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public EnumFacing getRenderFacing() {
        return this.getState().getValue(BlockStateProperties.FACING);
    }
}
