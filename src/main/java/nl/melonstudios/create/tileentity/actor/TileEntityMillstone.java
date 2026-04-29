package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import com.melonstudios.melonlib.network.TrackedByteBuf;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.recipe.server.MillingRecipes;
import nl.melonstudios.create.recipe.PulverizationRecipe;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.util.Utils;

import javax.annotation.Nullable;
import java.io.IOException;

public class TileEntityMillstone extends TileEntityKinetic implements IItemHandler {
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
            if (stack.getCount() < stack.getMaxStackSize()) continue;
            return;
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
            if (stack.isEmpty()) continue;
            for (int i = 0; i < this.output.length; i++) {
                if (this.output[i].isEmpty()) {
                    this.output[i] = stack;
                    continue stacks;
                }
                if (ItemStack.areItemsEqual(this.output[i], stack) && ItemStack.areItemStackTagsEqual(this.output[i], stack)) {
                    int space = this.output[i].getMaxStackSize() - this.output[i].getCount();
                    if (space <= 0) continue;
                    space = Math.min(space, stack.getCount());
                    this.output[i].grow(space);
                    stack.shrink(space);
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
    public void writePacket(TrackedByteBuf buf) throws IOException {
        super.writePacket(buf);

        if (!this.input.isEmpty()) {
            buf.writeBoolean(true);
            StackUtil.writeItemStack(this.input, buf, true, true);
        } else buf.writeBoolean(false);
        for (int i = 0; i < this.output.length; i++) {
            if (!this.output[i].isEmpty()) {
                buf.writeBoolean(true);
                StackUtil.writeItemStack(this.output[i], buf, true, true);
            } else buf.writeBoolean(false);
        }
        buf.writeInt(this.timer);
    }
    @Override
    public void readPacket(ByteBuf buf) throws IOException {
        super.readPacket(buf);

        if (buf.readBoolean()) {
            this.input = StackUtil.readItemStack(buf, true, true);
        } else this.input = ItemStack.EMPTY;
        for (int i = 0; i < this.output.length; i++) {
            if (buf.readBoolean()) {
                this.output[i] = StackUtil.readItemStack(buf, true, true);
            } else this.output[i] = ItemStack.EMPTY;
        }
        this.timer = buf.readInt();
    }

    @Override
    public int getSlots() {
        return this.output.length + 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return slot == 0 ? this.input : this.output[slot-1];
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (slot != 0 || stack.isEmpty() || !this.input.isEmpty() || MillingRecipes.instance.getRecipeForInput(stack) == null) return stack;
        if (simulate) return ItemStack.EMPTY;
        this.input = stack.copy();
        this.sync();
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot == 0 || amount == 0) return ItemStack.EMPTY;
        ItemStack prev = simulate ? this.output[slot-1].copy() : this.output[slot-1];
        if (!simulate) this.sync();
        return prev.splitStack(amount);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return slot == 0 && MillingRecipes.instance.getRecipeForInput(stack) != null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)this;
        return super.getCapability(capability, facing);
    }
}
