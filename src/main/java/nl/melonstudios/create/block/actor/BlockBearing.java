package nl.melonstudios.create.block.actor;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.actor.TileEntityBearing;

import javax.annotation.Nullable;

public class BlockBearing extends BlockBearingBase {
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBearing();
    }
}
