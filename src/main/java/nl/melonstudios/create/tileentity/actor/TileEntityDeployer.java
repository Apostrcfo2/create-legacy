package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.AABB;
import com.melonstudios.melonlib.misc.StackUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import nl.melonstudios.create.block.BlockClutch;
import nl.melonstudios.create.block.BlockKineticBase;
import nl.melonstudios.create.block.actor.BlockDeployer;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventory;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ISidedInventoryDebloated;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.BlockRotationHelper;
import nl.melonstudios.create.util.PlayerDeployer;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.Utils;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityDeployer extends TileEntityKinetic implements IContraptionActor, ISidedInventoryDebloated, ITileEntityWithSubInteractions {
    private PlayerDeployer player;
    public TileEntityDeployer() {
        this.createInteractions();
    }

    @Override
    public void setWorld(World worldIn) {
        this.world = worldIn;
        this.player = new PlayerDeployer(this);
        this.player.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
    }

    @Override
    public void setPos(BlockPos posIn) {
        super.setPos(posIn);
        if (this.player != null)
            this.player.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
    }

    private final Vector3f itemUsePosOld = new Vector3f();
    private final Vector3f itemUsePos = new Vector3f();
    private static BlockPos pos(Vector3f vec) {
        return new BlockPos(vec.x, vec.y, vec.z);
    }
    private boolean checkForRedstone() {
        return !this.world.isBlockPowered(this.pos) && this.world.isBlockIndirectlyGettingPowered(this.pos) == 0;
    }

    public int progressOld = 0;
    public int progress = 0;
    @Override
    public void tick() {
        super.tick();

        this.progressOld = this.progress;
        if (this.speed != 0.0F) {
            if (this.progress != 0 || (this.cloggedItem.isEmpty() && this.checkForRedstone())) {
                this.progress += (int) Math.abs(this.speed);
                if (this.progressOld < 1000 && this.progress >= 1000) {
                    EnumFacing facing = this.getState().getValue(BlockDeployer.FACING);
                    this.itemUsePos.set(
                            this.pos.getX() + 0.5F + facing.getFrontOffsetX() * 2,
                            this.pos.getY() + 0.5F + facing.getFrontOffsetY() * 2,
                            this.pos.getZ() + 0.5F + facing.getFrontOffsetZ() * 2
                    );
                    BlockPos use = pos(this.itemUsePos);
                    this.player.setPosition(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5);
                    if (!this.heldItem.isEmpty()) {
                        if (this.heldItem.getItem() instanceof ItemBlock) {
                            IBlockState pre = world.getBlockState(use);
                            if (pre.getBlock().isReplaceable(world, use)) {
                                ItemBlock ib = (ItemBlock) this.heldItem.getItem();
                                IBlockState placed = ib.getBlock().getStateForPlacement(world, use, facing.getOpposite(),
                                        this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z, this.heldItem.getMetadata(),
                                        this.player, EnumHand.MAIN_HAND);
                                if (placed.getBlock().canPlaceBlockAt(world, use)) {
                                    ib.placeBlockAt(this.heldItem, this.player, world, use, facing.getOpposite(),
                                            this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z, placed);
                                    SoundType st = placed.getBlock().getSoundType(placed, world, use, null);
                                    world.playSound(null, use, st.getPlaceSound(), SoundCategory.BLOCKS,
                                            (1.0F + st.getVolume()) * 0.5F, 0.9F + world.rand.nextFloat() * 0.2F);
                                    this.heldItem.shrink(1);
                                }
                            }
                        } else {
                            this.heldItem.onItemUse(this.player, world, use,
                                    EnumHand.MAIN_HAND, facing.getOpposite(),
                                    this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z
                            );
                        }
                    } else {
                        IBlockState state = this.world.getBlockState(use);
                        state.getBlock().onBlockActivated(
                                this.world, use, state, this.player, EnumHand.MAIN_HAND, facing.getOpposite(),
                                this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z
                        );
                    }
                    this.sync();
                }
                if (this.progress > 2000) {
                    this.progress = 0;
                }
            }
            this.markDirty();
        }
    }

    public boolean skipRenderItem = false;
    @Override
    public void setOnContraption(boolean onContraption) {
        this.speed = onContraption ? 64.0F : 0.0F;
        this.progress = 0;
        this.skipRenderItem = onContraption;
    }

    @Override
    public void contraptionTick(IContraptionAccessor contraption, World world,
                                Vector3f position, BlockPos blockPosition, boolean moved, Vector3f movement) {
        this.progressOld = this.progress;
        this.progress += (int) Math.abs(this.speed);
        if (this.progress > 2000) this.progress = 0;
        if (world.isRemote) return;

        this.player.setPosition(position.x, position.y, position.z);
        ContraptionInventory inventory = contraption.getInventory();

        this.itemUsePosOld.set(this.itemUsePos);
        EnumFacing facing = this.getState().getValue(BlockDeployer.FACING);
        contraption.getWorldPos(this.pos.offset(facing, 2), this.itemUsePos);
        BlockPos use = pos(this.itemUsePos);
        if (!pos(this.itemUsePosOld).equals(use)) {
            if (this.heldItem.isEmpty()) {
                this.heldItem = inventory.retrieveItem(this.filter);
            }
            Vector3f facingVec = new Vector3f();
            contraption.getNormal(facing, facingVec);
            facingVec.normalise();
            final EnumFacing vecFacing = getFacingFromVector3f(facingVec);
            if (!this.heldItem.isEmpty()) {
                if (this.heldItem.getItem() instanceof ItemBlock) {
                    IBlockState pre = world.getBlockState(use);
                    if (pre.getBlock().isReplaceable(world, use)) {
                        ItemBlock ib = (ItemBlock) this.heldItem.getItem();
                        IBlockState placed = ib.getBlock().getStateForPlacement(world, use, vecFacing.getOpposite(),
                                this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z, this.heldItem.getMetadata(),
                                this.player, EnumHand.MAIN_HAND);
                        if (placed.getBlock().canPlaceBlockAt(world, use)) {
                            if (ib.placeBlockAt(this.heldItem, this.player, world, use, vecFacing.getOpposite(),
                                    this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z, placed)) {
                                SoundType st = placed.getBlock().getSoundType(placed, world, use, null);
                                world.playSound(null, use, st.getPlaceSound(), SoundCategory.BLOCKS,
                                        (1.0F + st.getVolume()) * 0.5F, 0.9F + world.rand.nextFloat() * 0.2F);
                                this.heldItem.shrink(1);
                            }
                        }
                    }
                } else {
                    this.heldItem.onItemUse(this.player, world, use,
                            EnumHand.MAIN_HAND, vecFacing.getOpposite(),
                            this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z
                    );
                }
            } else {
                IBlockState state = world.getBlockState(use);
                state.getBlock().onBlockActivated(world, use, state, this.player, EnumHand.MAIN_HAND, vecFacing.getOpposite(),
                        this.itemUsePos.x, this.itemUsePos.y, this.itemUsePos.z);
            }
        }

        if (!this.cloggedItem.isEmpty()) {
            this.cloggedItem = inventory.insertItem(this.cloggedItem);
        }
    }

    private static EnumFacing getFacingFromVector3f(Vector3f facingVec) {
        EnumFacing vecFacing;
        if (facingVec.x == 1.0F) {
            vecFacing = EnumFacing.EAST;
        } else if (facingVec.x == -1.0F) {
            vecFacing = EnumFacing.WEST;
        } else if (facingVec.z == 1.0F) {
            vecFacing = EnumFacing.SOUTH;
        } else if (facingVec.z == -1.0F) {
            vecFacing = EnumFacing.NORTH;
        } else if (facingVec.y == 1.0F) {
            vecFacing = EnumFacing.UP;
        } else if (facingVec.y == -1.0F) {
            vecFacing = EnumFacing.DOWN;
        } else {
            throw new IllegalStateException("Fix it pls " + facingVec);
        }
        return vecFacing;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        if (this.filter != null) {
            nbt.setTag("Filter", this.filter.serialize(new NBTTagCompound()));
        }
        if (!this.heldItem.isEmpty()) {
            nbt.setTag("HeldItem", this.heldItem.writeToNBT(new NBTTagCompound()));
        }
        if (!this.cloggedItem.isEmpty()) {
            nbt.setTag("CloggedItem", this.cloggedItem.writeToNBT(new NBTTagCompound()));
        }
        nbt.setInteger("progress", this.progress);

        return super.writeToNBT(nbt);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.filter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.filter = null;
        if (nbt.hasKey("HeldItem", 10)) {
            this.heldItem = new ItemStack(nbt.getCompoundTag("HeldItem"));
        } else this.heldItem = ItemStack.EMPTY;
        if (nbt.hasKey("CloggedItem", 10)) {
            this.cloggedItem = new ItemStack(nbt.getCompoundTag("CloggedItem"));
        } else this.cloggedItem = ItemStack.EMPTY;
        this.progress = nbt.getInteger("progress");
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (this.filter != null) {
            nbt.setTag("Filter", this.filter.serialize(new NBTTagCompound()));
        }
        if (!this.heldItem.isEmpty()) {
            nbt.setTag("Held", this.heldItem.writeToNBT(new NBTTagCompound()));
        }
        if (!this.cloggedItem.isEmpty()) {
            nbt.setTag("Clogged", this.cloggedItem.writeToNBT(new NBTTagCompound()));
        }
        if (this.progress != 0) nbt.setInteger("progress", this.progress);

        return nbt;
    }

    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.filter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.filter = null;
        if (nbt.hasKey("Held", 10)) {
            this.heldItem = new ItemStack(nbt.getCompoundTag("Held"));
        } else this.heldItem = ItemStack.EMPTY;
        if (nbt.hasKey("Clogged", 10)) {
            this.cloggedItem = new ItemStack(nbt.getCompoundTag("Clogged"));
        } else this.cloggedItem = ItemStack.EMPTY;
        this.progress = nbt.getInteger("progress");
    }

    public void setFilter(ItemStack stack) {
        if (stack.isEmpty()) {
            this.filter = null;
        } else {
            this.filter = new ItemFilterExact(stack);
        }
        this.sync();
    }

    public IItemFilter filter = null;
    public ItemStack heldItem = ItemStack.EMPTY;
    public ItemStack cloggedItem = ItemStack.EMPTY;
    private static final int[] SLOTS = new int[]{0,1};
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0 && (this.filter == null || this.filter.matches(itemStackIn));
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return index == 1;
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return this.heldItem.isEmpty() && this.cloggedItem.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.heldItem : this.cloggedItem;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack stack = this.getStackInSlot(index).splitStack(count);
        this.sync();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        ItemStack stack;
        if (index == 0) {
            stack = this.heldItem;
            this.heldItem = ItemStack.EMPTY;
        } else {
            stack = this.cloggedItem;
            this.cloggedItem = ItemStack.EMPTY;
        }
        this.sync();
        return stack;
    }
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            this.heldItem = stack;
        } else {
            this.cloggedItem = stack;
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
        this.heldItem = ItemStack.EMPTY;
        this.cloggedItem = ItemStack.EMPTY;
        this.sync();
    }
    @Override
    public String getName() {
        return "Deployer";
    }

    @Override
    public void destroy() {
        super.destroy();
        StackUtil.dropItemsAt(this.world, this.pos, this.heldItem, this.cloggedItem);
    }

    @Override
    protected AxisAlignedBB createRenderBoundingBox() {
        return AABB.wrap(this.pos, 1);
    }

    private final List<SubInteractionBox> interactionsX = new ArrayList<>();
    private final List<SubInteractionBox> interactionsY = new ArrayList<>();
    private final List<SubInteractionBox> interactionsZ = new ArrayList<>();
    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        IBlockState state = this.getState();
        EnumFacing facing = state.getValue(BlockDeployer.FACING);
        boolean rotated = state.getValue(BlockDeployer.ROTATED);
        return Utils.axis_choose(facing.rotateAround(BlockDeployer.getShaftAxis(facing, rotated)).getAxis(), this.interactionsX, this.interactionsY, this.interactionsZ);
    }

    private void createInteractions() {
        SetFilterInteraction interaction = new SetFilterInteraction(this);
        this.interactionsX.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.WEST, 0.25F, interaction));
        this.interactionsX.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.EAST, 0.25F, interaction));
        this.interactionsY.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.DOWN, 0.25F, interaction));
        this.interactionsY.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.UP, 0.25F, interaction));
        this.interactionsZ.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.NORTH, 0.25F, interaction));
        this.interactionsZ.add(SubInteractionBox.Helper.createCenteredSide(EnumFacing.SOUTH, 0.25F, interaction));
    }

    private static class SetFilterInteraction implements SubInteractionBox.Interaction {
        private final TileEntityDeployer te;
        private SetFilterInteraction(TileEntityDeployer te) {
            this.te = te;
        }

        @Override
        public boolean interact(@Nullable EntityPlayer player, boolean sneaking, ItemStack held) {
            this.te.setFilter(held);
            if (player != null) {
                if (held.isEmpty()) {
                    player.playSound(SoundEvents.ENTITY_ITEMFRAME_REMOVE_ITEM, 1.0F, 1.0F);
                    player.sendStatusMessage(new TextComponentString("Cleared item filter"), true);
                } else {
                    player.playSound(SoundEvents.ENTITY_ITEMFRAME_ADD_ITEM, 1.0F, 1.0F);
                    player.sendStatusMessage(new TextComponentString("Set item filter to " + held.getDisplayName()), true);
                }
            }
            return true;
        }
    }
}
