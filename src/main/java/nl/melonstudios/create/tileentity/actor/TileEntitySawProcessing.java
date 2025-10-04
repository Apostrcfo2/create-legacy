package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.recipe.CuttingRecipes;
import nl.melonstudios.create.recipe.SawingRecipe;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TileEntitySawProcessing extends TileEntityKinetic implements ITileEntityWithSubInteractions {
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
                    this.pushResult(result);
                    this.currentRecipe = null;
                    this.progress = 0;
                }
            } else {
                if (this.progress >= 20) {
                    this.pushResult(this.currentlyProcessing.copy());
                    this.currentlyProcessing = ItemStack.EMPTY;
                    this.currentRecipe = null;
                    this.progress = 0;
                }
            }
        }
    }

    private int getProgressTick() {
        return Math.max(1, MathHelper.floor(Math.abs(this.getSpeed()) / 16.0F));
    }
    private void pushResult(ItemStack stack) {
        if (this.world.isRemote) return;
        EnumFacing side = this.getProcessingDirection();
        EntityItem entity = new EntityItem(this.world,
                this.pos.getX() + 0.5 + side.getFrontOffsetX(),
                this.pos.getY() + 1.0F,
                this.pos.getZ() + 0.5 + side.getFrontOffsetZ(),
                stack
        );
        entity.motionX = entity.motionZ = 0;
        entity.motionY = 0.05;
        entity.setDefaultPickupDelay();
        this.world.spawnEntity(entity);
    }
    public EnumFacing getProcessingDirection() {
        boolean x = this.getBlockMetadata() == 4;
        return EnumFacing.NORTH;
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
        if (this.currentlyProcessing.isEmpty()) {
            this.setCurrentlyProcessing(entityItem.getItem().copy());
            entityItem.setDead();
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
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);

        if (this.recipeFilter != null) nbt.setTag("Filter", this.recipeFilter.serialize(new NBTTagCompound()));
        if (!this.currentlyProcessing.isEmpty()) nbt.setTag("CurrentlyProcessing", this.currentlyProcessing.writeToNBT(new NBTTagCompound()));
        if (this.currentRecipe != null) nbt.setString("currentRecipe", this.currentRecipe.recipeID);
        nbt.setInteger("progress", this.progress);

        return nbt;
    }
    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (this.recipeFilter != null) nbt.setTag("Filter", this.recipeFilter.serialize(new NBTTagCompound()));
        if (!this.currentlyProcessing.isEmpty()) nbt.setTag("CurrentlyProcessing", this.currentlyProcessing.writeToNBT(new NBTTagCompound()));
        if (this.currentRecipe != null) nbt.setString("currentRecipe", this.currentRecipe.recipeID);
        if (this.progress != 0) nbt.setInteger("progress", this.progress);

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
    }

    @Override
    public void destroy() {
        super.destroy();

        StackUtil.dropItemsAt(this.world, this.pos, this.currentlyProcessing.copy());
    }
}
