package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.util.Utils;
import nl.melonstudios.create.util.interfaces.ICogwheel;
import nl.melonstudios.create.util.interfaces.IRotate;

import java.util.LinkedList;

public class TileEntityCogwheel extends TileEntityKinetic {
    public boolean isLarge() {
        return ((ICogwheel)this.blockType).isLargeCog();
    }

    @SideOnly(Side.CLIENT)
    public EnumFacing.Axis getRenderAxis() {
        return this.getState().getValue(BlockStateProperties.AXIS);
    }

    @Override
    public LinkedList<BlockPos> addPropagationLocations(IRotate block, IBlockState state, LinkedList<BlockPos> neighbours) {
        super.addPropagationLocations(block, state, neighbours);

        if (!this.isLarge()) return neighbours;

        EnumFacing.Axis axis = this.getState().getValue(BlockStateProperties.AXIS);

        BlockPos pos1 = this.getPos().offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, axis));
        for (EnumFacing facing : Utils.getSurrounding(axis)) neighbours.add(pos1.offset(facing));
        BlockPos pos2 = this.getPos().offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, axis));
        for (EnumFacing facing : Utils.getSurrounding(axis)) neighbours.add(pos2.offset(facing));

        return neighbours;
    }
}
