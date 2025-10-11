package nl.melonstudios.create.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.extensions.IExtensionBlock;
import nl.melonstudios.create.extensions.IExtensionIBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(IBlockState.class)
public abstract class MixinIBlockState implements IExtensionIBlockState {
    @Shadow
    public abstract Block getBlock();

    @Override
    public boolean create$isSideSticky(EnumFacing side) {
        return ((IExtensionBlock)this.getBlock()).create$isSideSticky((IBlockState) this, side);
    }

    @Override
    public void create$addStickyLocations(World world, BlockPos pos, EnumFacing side, List<BlockPos> positions) {
        ((IExtensionBlock)this.getBlock()).create$addStickyLocations(world, pos, (IBlockState)this, side, positions);
    }
}
