package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import org.lwjgl.util.vector.Vector3f;

public class TileEntityPlough extends TileEntity implements IContraptionActor {
    public TileEntityPlough() {

    }

    public boolean moving = false;
    @Override
    public void setOnContraption(boolean onContraption) {
        this.moving = onContraption;
    }
    @Override
    public boolean isOnContraption() {
        return this.moving;
    }

    @Override
    public void contraptionTick(IContraptionAccessor contraption, World world, Vector3f position, BlockPos blockPosition, boolean moved, Vector3f movement) {
        if (moved && !world.isRemote) {
            BlockPos soilPos = blockPosition.down();
            IBlockState soilState = world.getBlockState(soilPos);
            if (BlockDictionary.isBlockTagged(soilState, "create:plowable")) {
                world.playSound(null, soilPos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                world.setBlockState(soilPos, Blocks.FARMLAND.getDefaultState(), 3);
            }
        }
    }
}
