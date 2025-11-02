package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.StackUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.tileentity.marker.IInventoryDebloated;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityChute extends TileEntityOptimizedBase implements IInventoryDebloated {
    public float randomizedItemRotation;
    public TileEntityChute() {
        super();

        this.setTickRateLazy(10);
    }

    @Override
    public void initializeClient() {
        super.initializeClient();

        this.randomizedItemRotation = this.world.rand.nextInt(360);
    }

    public ItemStack stack = ItemStack.EMPTY;

    @Override
    public void tick() {

    }

    @Override
    public void tickLazy() {
        boolean mod = false;
        if (!this.stack.isEmpty()) {
            IInventory below = this.getInv(this.pos.down());
            if (below != null) {
                this.stack = TileEntityHopper.putStackInInventoryAllSlots(this, below, this.stack, EnumFacing.UP);
                mod = true;
            }
        }

        if (this.stack.isEmpty()) {
            IInventory above = this.getInv(this.pos.up());
            if (above != null) {
                for (int i = 0; i < above.getSizeInventory(); i++) {
                    if (!above.getStackInSlot(i).isEmpty()) {
                        this.stack = above.decrStackSize(i, 16);
                        mod = true;
                        break;
                    }
                }
            }
        }

        if (mod) this.sync();
    }

    @Nullable
    private IInventory getInv(BlockPos pos) {
        TileEntity te = this.world.getTileEntity(pos);
        return te instanceof IInventory ? (IInventory) te : null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (!this.stack.isEmpty()) {
            nbt.setTag("Stack", this.stack.writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("Stack", 10)) {
            this.stack = new ItemStack(nbt.getCompoundTag("Stack"));
        }
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        if (!this.stack.isEmpty()) {
            nbt.setTag("Stack", this.stack.writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        if (nbt.hasKey("Stack", 10)) {
            this.stack = new ItemStack(nbt.getCompoundTag("Stack"));
        }
    }

    @Override
    public String getName() {
        return "Chute";
    }
    @Override
    public int getSizeInventory() {
        return 1;
    }
    @Override
    public boolean isEmpty() {
        return this.stack.isEmpty();
    }
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.stack;
    }
    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack split = this.stack.splitStack(count);
        this.sync();
        return split;
    }
    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack split = this.stack.copy();
        this.stack = ItemStack.EMPTY;
        this.sync();
        return split;
    }
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.stack = stack;
        this.sync();
    }
    @Override
    public int getInventoryStackLimit() {
        return 16;
    }
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return true;
    }
    @Override
    public void clear() {
        this.stack = ItemStack.EMPTY;
        this.sync();
    }

    @Override
    public void destroy() {
        StackUtil.dropItemsAt(this.world, this.pos, this.stack.copy());
    }
}
