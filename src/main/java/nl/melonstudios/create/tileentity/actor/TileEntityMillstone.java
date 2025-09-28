package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.recipe.MillingRecipes;
import nl.melonstudios.create.recipe.PulverizationRecipe;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.util.Utils;

public class TileEntityMillstone extends TileEntityKinetic implements ISidedInventory {
    public ItemStack input = ItemStack.EMPTY;
    public final ItemStack[] output = new ItemStack[] {
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
    };

    public int timer;
    private PulverizationRecipe lastMillingRecipe = null;

    @Override
    public void tick() {
        super.tick();

        if (this.getSpeed() == 0) return;
        for (ItemStack stack : this.output) {
            if (stack.getCount() == stack.getMaxStackSize()) return;
        }

        if (this.timer > 0) {
            this.timer -= this.getProcessingSpeed();

            if (this.world.isRemote) {
                this.spawnParticles();
                return;
            }
            if (this.timer <= 0) {
                this.process();
            }
            this.markDirty();
            return;
        }

        if (this.input.isEmpty()) return;

        if (this.lastMillingRecipe == null || !this.lastMillingRecipe.input.test(this.input)) {
            PulverizationRecipe recipe = MillingRecipes.instance.getRecipeForInput(this.input);
            if (recipe != null) {
                this.lastMillingRecipe = recipe;
                this.timer = this.lastMillingRecipe.processingTime;
                this.sync();
            } else {
                this.timer = 100;
                this.sync();
            }
            return;
        }

        this.timer = this.lastMillingRecipe.processingTime;
        this.sync();
    }

    public int getProcessingSpeed() {
        return MathHelper.clamp((int)Math.abs(this.getSpeed() / 16.0F), 1, 512);
    }
    public void spawnParticles() {
        if (this.input.isEmpty()) return;

        if (this.world.getTotalWorldTime() % 5 == 0) {
            CreateLegacy.proxy.millstoneFX(this);
        }
    }
    private void process() {
        if (this.lastMillingRecipe == null || !this.lastMillingRecipe.input.test(this.input)) {
            PulverizationRecipe recipe = MillingRecipes.instance.getRecipeForInput(this.input);
            if (recipe == null) return;
            this.lastMillingRecipe = recipe;
        }

        this.input.shrink(1);
        stacks:
        for (ItemStack stack : Utils.rollChancedResults(this.lastMillingRecipe.results)) {
            for (int i = 0; i < this.output.length; i++) {
                if (this.output[i].isEmpty()) {
                    this.output[i] = stack;
                    continue stacks;
                }
                if (ItemStack.areItemStacksEqual(this.output[i], stack)) {
                    int space = this.output[i].getMaxStackSize() - this.output[i].getCount();
                    space = Math.min(space, stack.getCount());
                    this.output[i].grow(space);
                    stack.shrink(i);
                    if (stack.isEmpty()) continue stacks;
                }
            }
        }
        this.sync();
    }

    @Override
    public void destroy() {
        super.destroy();
        StackUtil.dropItemsAt(this.world, this.pos, this.input);
        StackUtil.dropItemsAt(this.world, this.pos, this.output);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        if (!this.input.isEmpty()) {
            nbt.setTag("Input", this.input.writeToNBT(new NBTTagCompound()));
        }
        for (int i = 0; i < this.output.length; i++) {
            ItemStack out = this.output[i];
            if (!out.isEmpty()) {
                nbt.setTag("Output_" + i, out.writeToNBT(new NBTTagCompound()));
            }
        }
        nbt.setInteger("timer", this.timer);

        return nbt;
    }
    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (!this.input.isEmpty()) {
            nbt.setTag("In", this.input.writeToNBT(new NBTTagCompound()));
        }
        for (int i = 0; i < this.output.length; i++) {
            ItemStack out = this.output[i];
            if (!out.isEmpty()) {
                nbt.setTag("Out" + i, out.writeToNBT(new NBTTagCompound()));
            }
        }
        nbt.setInteger("timer", this.timer);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("Input", 10)) {
            this.input = new ItemStack(compound.getCompoundTag("Input"));
        } else this.input = ItemStack.EMPTY;
        for (int i = 0; i < this.output.length; i++) {
            if (compound.hasKey("Output_" + i, 10)) {
                this.output[i] = new ItemStack(compound.getCompoundTag("Output_" + i));
            }
        }
        this.timer = compound.getInteger("timer");
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        if (nbt.hasKey("In", 10)) {
            this.input = new ItemStack(nbt.getCompoundTag("In"));
        } else this.input = ItemStack.EMPTY;
        for (int i = 0; i < this.output.length; i++) {
            if (nbt.hasKey("Out" + i, 10)) {
                this.output[i] = new ItemStack(nbt.getCompoundTag("Out" + i));
            }
        }
        this.timer = nbt.getInteger("timer");
    }

    private static final int[] SLOTS = {0,1,2,3,4,5,6,7,8,9};
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0 && this.input.isEmpty() && MillingRecipes.instance.getRecipeForInput(itemStackIn) != null;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index != 0 && direction != EnumFacing.UP;
    }

    @Override
    public int getSizeInventory() {
        return 10;
    }

    @Override
    public boolean isEmpty() {
        if (!this.input.isEmpty()) return false;
        for (int i = 0; i < 9; i++) if (!this.output[i].isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.input : this.output[index-1];
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0) {
            ItemStack ret = this.input.splitStack(count);
            this.sync();
            return ret;
        }
        ItemStack ret = this.output[index-1].splitStack(count);
        this.sync();
        return ret;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0) {
            ItemStack ret = this.input.copy();
            this.input = ItemStack.EMPTY;
            this.sync();
            return ret;
        }
        ItemStack ret = this.output[index-1].copy();
        this.output[index-1] = ItemStack.EMPTY;
        this.sync();
        return ret;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            this.input = stack;
            this.sync();
        } else {
            this.output[index - 1] = stack;
            this.sync();
        }
    }
    @Override
    public int getInventoryStackLimit() {
        return 64;
    }
    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return false;
    }
    @Override
    public void openInventory(EntityPlayer player) {

    }
    @Override
    public void closeInventory(EntityPlayer player) {

    }
    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0 && MillingRecipes.instance.getRecipeForInput(stack) != null;
    }
    @Override
    public int getField(int id) {
        return 0;
    }
    @Override
    public void setField(int id, int value) {

    }
    @Override
    public int getFieldCount() {
        return 0;
    }
    @Override
    public void clear() {
        this.input = ItemStack.EMPTY;
        for (int i = 0; i < 9; i++) this.output[i] = ItemStack.EMPTY;
        this.sync();
    }
    @Override
    public String getName() {
        return "Millstone";
    }
    @Override
    public boolean hasCustomName() {
        return false;
    }
}
