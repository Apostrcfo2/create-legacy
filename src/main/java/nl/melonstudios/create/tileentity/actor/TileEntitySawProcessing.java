package nl.melonstudios.create.tileentity.actor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.TextComponentString;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;
import nl.melonstudios.create.util.filter.IItemFilter;
import nl.melonstudios.create.util.filter.ItemFilterExact;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;

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

        return nbt;
    }
    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();

        if (this.recipeFilter != null) nbt.setTag("Filter", this.recipeFilter.serialize(new NBTTagCompound()));

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.recipeFilter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.recipeFilter = null;
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);

        if (nbt.hasKey("Filter", 10)) {
            this.recipeFilter = IItemFilter.deserialize(nbt.getCompoundTag("Filter"));
        } else this.recipeFilter = null;
    }
}
