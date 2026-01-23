package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import nl.melonstudios.create.tileentity.marker.IDepot;
import nl.melonstudios.create.tileentity.marker.ISidedInventoryDebloated;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;

import java.util.List;

public class TileEntityDepot extends TileEntityOptimizedBase implements ISidedInventoryDebloated, ITopOpenInventory, IDepot {
    public float randomizedItemRotation;
    public TileEntityDepot() {
        this.setTickRateLazy(10);
    }

    public ItemStack mainItem = ItemStack.EMPTY;
    public ItemStack[] additionalItems = {
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
    };

    @Override
    public void initializeClient() {
        super.initializeClient();

        this.randomizedItemRotation = this.world.rand.nextInt(360);
    }

    @Override
    public void tick() {

    }

    @Override
    public void tickLazy() {
        if (this.mainItem.isEmpty()) {
            AxisAlignedBB aabb = new AxisAlignedBB(this.pos);
            List<EntityItem> items = this.world.getEntities(EntityItem.class,
                    (entity) -> entity.getEntityBoundingBox().intersects(aabb));
            if (!items.isEmpty()) {
                EntityItem select = items.get(0);
                this.handleSteppedOn(select);
            }
        }
    }
    public void handleSteppedOn(EntityItem entityItem) {
        if (this.mainItem.isEmpty() && !entityItem.isDead && !entityItem.getItem().isEmpty()) {
            ItemStack over = this.tryInsertItem(entityItem.getItem());
            if (over.isEmpty()) {
                entityItem.setDead();
            } else entityItem.setItem(over);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        if (!this.mainItem.isEmpty()) nbt.setTag("MainItem", this.mainItem.writeToNBT(new NBTTagCompound()));
        for (int i = 0; i < 8; i++) {
            if (!this.additionalItems[i].isEmpty()) nbt.setTag("AdditionalItem" + i, this.additionalItems[i].writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("MainItem", 10)) {
            this.mainItem = new ItemStack(nbt.getCompoundTag("MainItem"));
        } else this.mainItem = ItemStack.EMPTY;
        for (int i = 0; i < 8; i++) {
            if (nbt.hasKey("AdditionalItem" + i, 10)) {
                this.additionalItems[i] = new ItemStack(nbt.getCompoundTag("AdditionalItem" + i));
            } else this.additionalItems[i] = ItemStack.EMPTY;
        }
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = new NBTTagCompound();

        if (!this.mainItem.isEmpty()) nbt.setTag("MainItem", this.mainItem.writeToNBT(new NBTTagCompound()));
        for (int i = 0; i < 8; i++) {
            if (!this.additionalItems[i].isEmpty()) nbt.setTag("AdditionalItem" + i, this.additionalItems[i].writeToNBT(new NBTTagCompound()));
        }

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        if (nbt.hasKey("MainItem", 10)) {
            this.mainItem = new ItemStack(nbt.getCompoundTag("MainItem"));
        } else this.mainItem = ItemStack.EMPTY;
        for (int i = 0; i < 8; i++) {
            if (nbt.hasKey("AdditionalItem" + i, 10)) {
                this.additionalItems[i] = new ItemStack(nbt.getCompoundTag("AdditionalItem" + i));
            } else this.additionalItems[i] = ItemStack.EMPTY;
        }
    }

    private static final int[] slots = {0,1,2,3,4,5,6,7,8};
    private static final int[] none = {};
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0 && this.mainItem.isEmpty();
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 9;
    }

    @Override
    public boolean isEmpty() {
        if (!this.mainItem.isEmpty()) return false;
        for (int i = 0; i < 8; i++) {
            if (!this.additionalItems[i].isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.mainItem : this.additionalItems[index-1];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0) {
            ItemStack stack = this.mainItem.splitStack(count);
            this.sync();
            return stack;
        }
        ItemStack stack = this.additionalItems[index-1].splitStack(count);
        this.sync();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0) {
            ItemStack stack = this.mainItem.copy();
            this.mainItem = ItemStack.EMPTY;
            this.sync();
            return stack;
        }
        ItemStack stack = this.additionalItems[index-1].copy();
        this.additionalItems[index-1] = ItemStack.EMPTY;
        this.sync();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            this.mainItem = stack;
        } else {
            this.additionalItems[index-1] = stack;
        }
        this.sync();
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }

    @Override
    public void clear() {
        this.mainItem = ItemStack.EMPTY;
        for (int i = 0; i < 8; i++) {
            this.additionalItems[i] = ItemStack.EMPTY;
        }
        this.sync();
    }

    @Override
    public String getName() {
        return "Depot";
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        if (!this.mainItem.isEmpty()) return stack;
        this.mainItem = stack.copy();
        this.sync();
        return ItemStack.EMPTY;
    }

    @Override
    public void destroy() {
        super.destroy();

        StackUtil.dropItemsAt(this.world, this.pos, this.mainItem);
        StackUtil.dropItemsAt(this.world, this.pos, this.additionalItems);
    }

    @Override
    public ItemStack getPresentedItem() {
        return this.mainItem;
    }

    @Override
    public void decreasePresentedAndAddOutput(ItemStack output) {
        this.mainItem.shrink(1);
        if (this.mainItem.isEmpty()) {
            this.mainItem = output;
        } else {
            for (int i = 0; i < 8; i++) {
                if (this.additionalItems[i].isEmpty()) {
                    this.additionalItems[i] = output;
                    output = null;
                    break;
                } else {
                    if (this.additionalItems[i].getCount() < this.additionalItems[i].getMaxStackSize()) {
                        int size = Math.min(this.additionalItems[i].getMaxStackSize() - this.additionalItems[i].getCount(), output.getCount());
                        if (ItemStack.areItemsEqual(this.additionalItems[i], output) &&
                                ItemStack.areItemStackTagsEqual(this.additionalItems[i], output)) {
                            this.additionalItems[i].grow(size);
                            output.shrink(size);
                            if (output.isEmpty()) {
                                output = null;
                                break;
                            }
                        }
                    }
                }
            }
            if (output != null && !this.world.isRemote) {
                StackUtil.spawnItemDefaultVelocity(
                        this.world,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 1.0,
                        this.pos.getZ() + 0.5,
                        output
                );
            }
        }
        this.sync();
    }

    @Override
    public double getItemHeight() {
        return 0.875;
    }

    @Override
    public boolean isWool() {
        return false;
    }

    @Override
    public ItemStack takePresented(int count) {
        this.sync();
        return this.mainItem.splitStack(count);
    }
}
