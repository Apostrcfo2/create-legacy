package nl.melonstudios.create.tileentity.actor;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockBeltStraight;
import nl.melonstudios.create.block.state.EnumBeltPart;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.IDepot;

public class TileEntityBeltStraight extends TileEntityBeltBase implements IDepot {
    public TileEntityBeltStraight() {
        super();
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

    //TODO: Redo the inventory so this will work

    @Override
    public ItemStack getPresentedItem() {
        return this.transport;
    }

    @Override
    public void decreasePresentedAndAddOutput(ItemStack output) {
        this.transport.shrink(1);
        this.queue = output;
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
        this.sync();
        return this.transport.splitStack(count);
    }
}
