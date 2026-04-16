package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nl.melonstudios.create.block.actor.BlockBeltBase;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;

import javax.annotation.Nullable;

public abstract class TileEntityBeltBase extends TileEntityKinetic implements ITopOpenInventory {
    private final InventoryManager[] inventories = new InventoryManager[7];

    public ItemStack queue = ItemStack.EMPTY;
    public ItemStack transport = ItemStack.EMPTY;

    public float queuePos = 0.0F;
    public float transportPos = 0.0F;
    public float queuePosOld = 0.0F;
    public float transportPosOld = 0.0F;

    public TileEntityBeltBase() {
        for (EnumFacing side : EnumFacing.VALUES) {
            this.inventories[side.getIndex()] = new InventoryManager(side);
        }
        this.inventories[6] = new InventoryManager(null);
    }

    protected BlockBeltBase block() {
        return (BlockBeltBase) this.getBlockType();
    }

    @Override
    public void tick() {
        super.tick();

        this.transportPosOld = this.transportPos;
        this.queuePosOld = this.queuePos;
        float speed = this.getSpeed() * 0.0625F * 0.05F;
        if (speed != 0.0F) {
            EnumFacing.Axis transportAxis = this.block().getTransportAxis(this.getState());
            if (transportAxis == EnumFacing.Axis.X) speed *= -1;
            EnumFacing positive = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, transportAxis);
            EnumFacing negative = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, transportAxis);

            if (!this.transport.isEmpty()) {
                this.transportPos += speed;
                if (this.transportPos >= 1.0F) {
                    this.transportPos = 1.0F;
                    if (!this.world.isRemote) {
                        BlockPos pos = this.getOffsetPosition(this.pos, positive);
                        TileEntity te = this.world.getTileEntity(pos);
                        if (te instanceof ITopOpenInventory) {
                            ITopOpenInventory inv = (ITopOpenInventory) te;
                            this.transport = inv.tryInsertItem(this.transport.copy(), negative);
                            this.sync();
                        } else {
                            IBlockState state = this.world.getBlockState(pos);
                            if (state.getMaterial().isReplaceable()) {
                                double dx = this.pos.getX() + 0.5 + positive.getFrontOffsetX() * 0.6;
                                double dy = this.pos.getY() + 0.85;
                                double dz = this.pos.getZ() + 0.5 + positive.getFrontOffsetZ() * 0.6;
                                StackUtil.spawnItemWithVelocity(this.world, dx, dy, dz, this.transport.copy(),
                                        positive.getFrontOffsetX() * Math.abs(speed),
                                        0.2,
                                        positive.getFrontOffsetZ() * Math.abs(speed)
                                );
                                this.transport = ItemStack.EMPTY;
                                this.transportPos = 0.0F;
                                this.sync();
                            }
                        }
                    }
                } else if (this.transportPos <= -1.0F) {
                    this.transportPos = -1.0F;
                    if (!this.world.isRemote) {
                        BlockPos pos = this.getOffsetPosition(this.pos, negative);
                        TileEntity te = this.world.getTileEntity(pos);
                        if (te instanceof ITopOpenInventory) {
                            ITopOpenInventory inv = (ITopOpenInventory) te;
                            this.transport = inv.tryInsertItem(this.transport, positive);
                            this.sync();
                        } else {
                            IBlockState state = this.world.getBlockState(pos);
                            if (state.getMaterial().isReplaceable()) {
                                double dx = this.pos.getX() + 0.5 + negative.getFrontOffsetX() * 0.6;
                                double dy = this.pos.getY() + 0.85;
                                double dz = this.pos.getZ() + 0.5 + negative.getFrontOffsetZ() * 0.6;
                                StackUtil.spawnItemWithVelocity(this.world, dx, dy, dz, this.transport.copy(),
                                        negative.getFrontOffsetX() * Math.abs(speed),
                                        0.2,
                                        negative.getFrontOffsetZ() * Math.abs(speed)
                                );
                                this.transport = ItemStack.EMPTY;
                                this.transportPos = 0.0F;
                                this.sync();
                            }
                        }
                    }
                }
            } else if (!this.queue.isEmpty()) {
                if (!this.world.isRemote) {
                    this.transport = this.queue;
                    this.queue = ItemStack.EMPTY;
                    this.transportPos = this.queuePos;
                    this.queuePos = 0.0F;
                    this.sync();
                }
            }
        }
    }

    @Override
    public void destroy() {
        StackUtil.dropItemsAt(this.world, this.pos, this.transport, this.queue);
        if (this.getState().getValue(BlockBeltBase.PART) != EnumBeltPart.MIDDLE) {
            StackUtil.dropItemsAt(this.world, this.pos, new ItemStack(BlockInit.SHAFT)); //TODO: simply have it place the original shaft
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return true;
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T)(facing == null ? this.inventories[6] : this.inventories[facing.getIndex()]);
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if (!this.transport.isEmpty()) {
            nbt.setFloat("transportPos", this.transportPos);
            nbt.setTag("Transport", this.transport.serializeNBT());
        }
        if (!this.queue.isEmpty()) {
            nbt.setFloat("queuePos", this.queuePos);
            nbt.setTag("Queue", this.queue.serializeNBT());
        }

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Transport", 10)) {
            this.transportPosOld = this.transportPos = nbt.getFloat("transportPos");
            this.transport = new ItemStack(nbt.getCompoundTag("Transport"));
        } else this.transport = ItemStack.EMPTY;
        if (nbt.hasKey("Queue", 10)) {
            this.queuePosOld = this.queuePos = nbt.getFloat("queuePos");
            this.queue = new ItemStack(nbt.getCompoundTag("Queue"));
        } else this.queue = ItemStack.EMPTY;
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (!this.transport.isEmpty()) {
            nbt.setFloat("transportPos", this.transportPos);
            nbt.setTag("Transport", this.transport.serializeNBT());
        }
        if (!this.queue.isEmpty()) {
            nbt.setFloat("queuePos", this.queuePos);
            nbt.setTag("Queue", this.queue.serializeNBT());
        }

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        if (nbt.hasKey("Transport", 10)) {
            this.transportPosOld = this.transportPos = nbt.getFloat("transportPos");
            this.transport = new ItemStack(nbt.getCompoundTag("Transport"));
        } else this.transport = ItemStack.EMPTY;
        if (nbt.hasKey("Queue", 10)) {
            this.queuePosOld = this.queuePos = nbt.getFloat("queuePos");
            this.queue = new ItemStack(nbt.getCompoundTag("Queue"));
        } else this.queue = ItemStack.EMPTY;
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack) {
        if (this.block().isFunctional(this.getState())) {
            //if (this.transport.isEmpty()) {
            //    this.transport = stack;
            //    this.transportPosOld = this.transportPos = 0.0F;
            //    this.sync();
            //    return ItemStack.EMPTY;
            //}
            if (this.queue.isEmpty()) {
                this.queue = stack;
                this.queuePosOld = this.queuePos = 0.0F;
                this.sync();
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack, @Nullable EnumFacing side) {
        if (this.block().isFunctional(this.getState())) {
            //if (side == null || side.getAxis() == EnumFacing.Axis.Y) return this.tryInsertItem(stack);
            EnumFacing.Axis axis = this.block().getTransportAxis(this.getState());
            float position = (side == null || side.getAxis() == EnumFacing.Axis.Y || side.getAxis() != axis) ? 0.0F : side.getAxisDirection().getOffset();
            //if (this.transport.isEmpty()) {
            //    this.transport = stack;
            //    this.transportPosOld = this.transportPos = position;
            //    this.sync();
            //    return ItemStack.EMPTY;
            //}
            if (this.queue.isEmpty()) {
                this.queue = stack;
                this.queuePosOld = this.queuePos = position;
                this.sync();
                return ItemStack.EMPTY;
            }
        }
        return stack;
    }

    protected BlockPos getOffsetPosition(BlockPos pos, EnumFacing side) {
        return pos.offset(side);
    }

    public double getTransportPos(float delta) {
        return MathHelper.clampedLerp(this.transportPosOld, this.transportPos, delta) * 0.5;
    }
    public double getQueuePos(float delta) {
        return MathHelper.clampedLerp(this.queuePosOld, this.queuePos, delta) * 0.5;
    }

    private class InventoryManager implements IItemHandler {
        private final EnumFacing side;
        private InventoryManager(@Nullable EnumFacing side) {
            this.side = side;
        }

        private TileEntityBeltBase self() {
            return TileEntityBeltBase.this;
        }

        @Override
        public int getSlots() {
            return 2;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? self().queue : self().transport;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot == 1 || stack.isEmpty() || !self().queue.isEmpty()) return stack;
            return simulate ? ItemStack.EMPTY : self().tryInsertItem(stack, this.side);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 0 || amount <= 0 || self().transport.isEmpty()) return ItemStack.EMPTY;
            ItemStack stack = self().transport.copy();
            ItemStack split = stack.splitStack(amount);
            if (simulate) return split;
            self().transport = stack;
            self().sync();
            return split;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }
    }
}
