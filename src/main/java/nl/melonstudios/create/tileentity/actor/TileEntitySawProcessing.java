package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.recipe.CuttingRecipes;
import nl.melonstudios.create.recipe.SawingRecipe;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ISidedInventoryDebloated;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.tileentity.marker.ITopOpenInventory;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class TileEntitySawProcessing extends TileEntityKinetic implements ITileEntityWithSubInteractions, ITopOpenInventory, ISidedInventoryDebloated {
    public static void addSubInteractionsAlongX(TileEntitySawProcessing te) {
        te.subInteractionBoxes.add(SubInteractionBox.Helper.createDefaultAt(0.25F, 0.75F, 0.5F, te::setFilter));
        te.subInteractionBoxes.add(SubInteractionBox.Helper.createDefaultAt(0.75F, 0.75F, 0.5F, te::setFilter));
    }
    public static void addSubInteractionsAlongZ(TileEntitySawProcessing te) {
        te.subInteractionBoxes.add(SubInteractionBox.Helper.createDefaultAt(0.5F, 0.75F, 0.25F, te::setFilter));
        te.subInteractionBoxes.add(SubInteractionBox.Helper.createDefaultAt(0.5F, 0.75F, 0.75F, te::setFilter));
    }

    public TileEntitySawProcessing() {
        super();
    }

    public ItemStack currentlyProcessing = ItemStack.EMPTY;
    public SawingRecipe currentRecipe;
    public int progress;
    public int lastProgress;
    public ItemStack outputQueue = ItemStack.EMPTY;

    @Override
    public void initialize() {
        super.initialize();

        if (this.getBlockMetadata() == 4) addSubInteractionsAlongX(this);
        else addSubInteractionsAlongZ(this);
    }

    @Override
    public void initializeClient() {
        super.initializeClient();

        if (this.getBlockMetadata() == 4) addSubInteractionsAlongX(this);
        else addSubInteractionsAlongZ(this);
    }

    @Override
    public void tick() {
        super.tick();

        this.lastProgress = this.progress;
        if (!this.currentlyProcessing.isEmpty() && this.getSpeed() != 0.0F) {
            this.markDirty();
            this.progress += this.getProgressTick();
            EnumFacing side = this.getProcessingDirection();
            if (this.world.isRemote) {
                CreateLegacy.proxy.spawnItemFX(
                        this.world,
                        this.pos.getX() + 0.5,
                        this.pos.getY() + 0.75,
                        this.pos.getZ() + 0.5,
                        -side.getFrontOffsetX() * (Math.abs(this.getSpeed()) * 0.0009765625),
                        0.05 + this.world.rand.nextDouble() * 0.1,
                        -side.getFrontOffsetZ() * (Math.abs(this.getSpeed()) * 0.0009765625),
                        this.currentlyProcessing
                );
            }
            if (this.currentRecipe != null) {
                if (this.progress >= this.currentRecipe.processingTime * this.currentlyProcessing.getCount()) {
                    int size = this.currentlyProcessing.getCount();
                    this.currentlyProcessing = ItemStack.EMPTY;
                    ItemStack result = this.currentRecipe.result.copy();
                    result.setCount(size * result.getCount());
                    this.outputQueue = result;
                    this.currentRecipe = null;
                    this.progress = 0;
                }
            } else {
                if (this.progress >= this.getProgressTick() * 10) {
                    this.outputQueue = this.currentlyProcessing.copy();
                    this.currentlyProcessing = ItemStack.EMPTY;
                    this.currentRecipe = null;
                    this.progress = 0;
                }
            }
        }
        if (!this.outputQueue.isEmpty() && this.getSpeed() != 0.0F) {
            this.pushResult();
        }
    }

    public int getProgressTick() {
        return Math.max(1, MathHelper.floor(Math.abs(this.getSpeed())  * 0.125F));
    }
    private void pushResult() {
        if (this.world.isRemote) return;
        EnumFacing side = this.getProcessingDirection();
        BlockPos drop = this.pos.offset(side);
        if (this.world.getBlockState(drop).getBlock().isReplaceable(this.world, drop)) {
            EntityItem entity = new EntityItem(this.world,
                    this.pos.getX() + 0.5 + side.getFrontOffsetX() * 0.65,
                    this.pos.getY() + 0.75F,
                    this.pos.getZ() + 0.5 + side.getFrontOffsetZ() * 0.65,
                    this.outputQueue.copy()
            );
            entity.motionX = side.getFrontOffsetX() * 0.1;
            entity.motionZ = side.getFrontOffsetZ() * 0.1;
            entity.motionY = 0.05;
            entity.setDefaultPickupDelay();
            this.world.spawnEntity(entity);
            this.outputQueue = ItemStack.EMPTY;
            this.sync();
        } else {
            TileEntity te = this.world.getTileEntity(drop);
            if (te instanceof ITopOpenInventory) {
                this.outputQueue = ((ITopOpenInventory)te).tryInsertItem(this.outputQueue);
                this.sync();
            }
        }
    }
    public EnumFacing getProcessingDirection() {
        boolean x = this.getBlockMetadata() == 4;
        if (x) return this.getSpeed() > 0.0F ? EnumFacing.NORTH : EnumFacing.SOUTH;
        else return this.getSpeed() > 0.0F ? EnumFacing.EAST : EnumFacing.WEST;
    }

    @Override
    public void tickLazy() {
        super.tickLazy();

        if (this.currentlyProcessing.isEmpty()) {
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
        if (this.currentlyProcessing.isEmpty() && this.outputQueue.isEmpty() && !entityItem.isDead && !entityItem.getItem().isEmpty()) {
            ItemStack over = this.tryInsertItem(entityItem.getItem());
            if (over.isEmpty()) {
                entityItem.setDead();
            } else entityItem.setItem(over);
        }
    }
    public void setCurrentlyProcessing(ItemStack stack) {
        this.currentlyProcessing = stack;
        this.currentRecipe = CuttingRecipes.instance.getRecipeForInput(this.currentlyProcessing, this.recipeFilter, 0);
        this.progress = 0;
        this.sync();
    }

    private final ArrayList<SubInteractionBox> subInteractionBoxes = new ArrayList<>();
    @Nullable
    public IItemFilter recipeFilter = null;

    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        return this.subInteractionBoxes;
    }

    private boolean setFilter(@Nullable EntityPlayer player, boolean sneaking, ItemStack held) {
        if (sneaking) return false;
        ItemStack copy = held.copy();
        if (held.isEmpty()) this.recipeFilter = null;
        else this.recipeFilter = new ItemFilterExact(copy);
        this.sync();
        if (player != null) {
            if (held.isEmpty()) {
                player.sendStatusMessage(new TextComponentString("Cleared recipe filter"), true);
            } else {
                player.sendStatusMessage(new TextComponentString("Set recipe filter to " + copy.getDisplayName()), true);
            }
        }
        return true;
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        if (this.recipeFilter != null) nbt.setTag("Filter", this.recipeFilter.serialize(new NBTTagCompound()));
        if (!this.currentlyProcessing.isEmpty()) nbt.setTag("CurrentlyProcessing", this.currentlyProcessing.writeToNBT(new NBTTagCompound()));
        if (this.currentRecipe != null) nbt.setString("currentRecipe", this.currentRecipe.recipeID);
        nbt.setInteger("progress", this.progress);
        if (!this.outputQueue.isEmpty()) nbt.setTag("OutputQueue", this.outputQueue.writeToNBT(new NBTTagCompound()));

        return nbt;
    }
    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (this.recipeFilter != null) nbt.setTag("Filter", this.recipeFilter.serialize(new NBTTagCompound()));
        if (!this.currentlyProcessing.isEmpty()) nbt.setTag("CurrentlyProcessing", this.currentlyProcessing.writeToNBT(new NBTTagCompound()));
        if (this.currentRecipe != null) nbt.setString("currentRecipe", this.currentRecipe.recipeID);
        if (this.progress != 0) nbt.setInteger("progress", this.progress);
        if (!this.outputQueue.isEmpty()) nbt.setTag("OutputQueue", this.outputQueue.writeToNBT(new NBTTagCompound()));

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.recipeFilter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.recipeFilter = null;
        if (nbt.hasKey("CurrentlyProcessing", 10)) {
            this.currentlyProcessing = new ItemStack(nbt.getCompoundTag("CurrentlyProcessing"));
        } else this.currentlyProcessing = ItemStack.EMPTY;
        if (nbt.hasKey("currentRecipe")) {
            this.currentRecipe = CuttingRecipes.instance.getRecipe(nbt.getString("currentRecipe"));
        } else this.currentRecipe = null;
        this.progress = nbt.getInteger("progress");
        if (nbt.hasKey("OutputQueue", 10)) {
            this.outputQueue = new ItemStack(nbt.getCompoundTag("OutputQueue"));
        } else this.outputQueue = ItemStack.EMPTY;
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.recipeFilter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.recipeFilter = null;
        if (nbt.hasKey("CurrentlyProcessing", 10)) {
            this.currentlyProcessing = new ItemStack(nbt.getCompoundTag("CurrentlyProcessing"));
        } else this.currentlyProcessing = ItemStack.EMPTY;
        if (nbt.hasKey("currentRecipe")) {
            this.currentRecipe = CuttingRecipes.instance.getRecipe(nbt.getString("currentRecipe"));
        } else this.currentRecipe = null;
        this.progress = nbt.getInteger("progress");
        if (nbt.hasKey("OutputQueue", 10)) {
            this.outputQueue = new ItemStack(nbt.getCompoundTag("OutputQueue"));
        } else this.outputQueue = ItemStack.EMPTY;
    }

    @Override
    public void destroy() {
        super.destroy();

        StackUtil.dropItemsAt(this.world, this.pos, this.currentlyProcessing.copy(), this.outputQueue.copy());
    }

    @Override
    public ItemStack tryInsertItem(ItemStack stack) {
        if (!this.outputQueue.isEmpty() || !this.currentlyProcessing.isEmpty()) return stack;
        ItemStack copy = stack.copy();
        copy.setCount(1);
        this.setCurrentlyProcessing(copy);
        ItemStack ret = stack.copy();
        ret.shrink(1);
        return ret.isEmpty() ? ItemStack.EMPTY : ret;
    }

    private static final int[] SLOTS = {0,1};
    private static final int[] NONE = {};
    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return side == EnumFacing.UP ? NONE : SLOTS;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return direction != EnumFacing.UP && index == 0 && (this.currentlyProcessing.isEmpty() && this.outputQueue.isEmpty());
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return direction != EnumFacing.UP && index == 1 && !this.outputQueue.isEmpty();
    }

    @Override
    public int getSizeInventory() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return (this.currentlyProcessing.isEmpty() && this.outputQueue.isEmpty());
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return index == 0 ? this.currentlyProcessing : this.outputQueue;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 0 || this.outputQueue.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.outputQueue.splitStack(count);
        this.sync();
        return stack;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 0 || this.outputQueue.isEmpty()) return ItemStack.EMPTY;
        ItemStack stack = this.outputQueue.copy();
        this.outputQueue = ItemStack.EMPTY;
        this.sync();
        return stack;
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            this.setCurrentlyProcessing(stack.copy());
        }
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
        this.currentlyProcessing = ItemStack.EMPTY;
        this.outputQueue = ItemStack.EMPTY;
        this.progress = 0;
        this.sync();
    }

    @Override
    public String getName() {
        return "Mechanical Saw (Processing)";
    }
}
