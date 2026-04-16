package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockBeltStraight;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.IDepot;

public class TileEntityBeltStraight extends TileEntityBeltBase implements IDepot {
    public Boolean lastRotatory = null;

    public TileEntityBeltStraight() {
        super();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    public float propagateRotationTo(TileEntityKinetic target, IBlockState stateFrom, IBlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (target instanceof TileEntityBeltStraight && !connectedViaAxes) {
            if (stateFrom.getValue(BlockBeltStraight.AXIS) != stateTo.getValue(BlockBeltStraight.AXIS)) return 0.0F;
            if (stateFrom.getValue(BlockBeltStraight.VERTICAL) != stateTo.getValue(BlockBeltStraight.VERTICAL)) return 0.0F;
            EnumFacing.Axis axis = stateFrom.getValue(BlockBeltStraight.AXIS);
            boolean vertical = stateFrom.getValue(BlockBeltStraight.VERTICAL);
            EnumBeltPart part = stateFrom.getValue(BlockBeltStraight.PART);
            if (diff.getX() != 0) {
                if (axis != EnumFacing.Axis.X || vertical) return 0.0F;
                if (diff.getX() > 0 && part == EnumBeltPart.END) return 0.0F;
                if (diff.getX() < 0 && part == EnumBeltPart.START) return 0.0F;
                return 1.0F;
            }
            if (diff.getZ() != 0) {
                if (axis != EnumFacing.Axis.Z || vertical) return 0.0F;
                if (diff.getZ() > 0 && part == EnumBeltPart.END) return 0.0F;
                if (diff.getZ() < 0 && part == EnumBeltPart.START) return 0.0F;
                return 1.0F;
            }
            if (diff.getY() != 0) {
                if (!vertical) return 0.0F;
                if (diff.getZ() > 0 && part == EnumBeltPart.END) return 0.0F;
                if (diff.getZ() < 0 && part == EnumBeltPart.START) return 0.0F;
                return 1.0F;
            }
        }
        return 0.0F;
    }

    @Override
    protected boolean flipped() {
        return this.getState().getValue(BlockBeltStraight.AXIS) == EnumFacing.Axis.X;
    }

    //TODO: Redo the inventory so this will work

    @Override
    public ItemStack getPresentedItem() {
        if (this.getSpeed() == 0.0F) return ItemStack.EMPTY;
        return this.getFlag() ? this.left : this.right;
    }

    @Override
    public void decreasePresentedAndAddOutput(ItemStack output) {
        if (this.getSpeed() == 0.0F) throw new UnsupportedOperationException("How did this even happen?");
        if (this.getFlag()) {
            this.left.shrink(1);
            if (this.right.isEmpty()) this.right = output.copy();
            else if (ItemStack.areItemsEqual(this.right, output) && ItemStack.areItemStackTagsEqual(this.right, output)) this.right.grow(output.getCount());
            else StackUtil.spawnItemWithVelocity(this.world,
                        this.pos.getX() + 0.5, this.pos.getY() + 1.0, this.pos.getZ() + 0.5,
                        output, this.world.rand.nextGaussian(), 0.4, this.world.rand.nextGaussian());
        }
        this.sync();
    }

    @Override
    public double getItemHeight() {
        return 0.75;
    }

    @Override
    public boolean isWool() {
        return true;
    }

    @Override
    public ItemStack takePresented(int count) {
        if (this.getSpeed() == 0.0F) return ItemStack.EMPTY;
        this.sync();
        return this.getFlag() ? this.left.splitStack(count) : this.right.splitStack(count);
    }
}
