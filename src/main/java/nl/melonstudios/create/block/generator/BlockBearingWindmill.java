package nl.melonstudios.create.block.generator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.tileentity.generator.TileEntityBearingWindmill;

import javax.annotation.Nullable;

public class BlockBearingWindmill extends BlockBearingBase {
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBearingWindmill();
    }
}
