package nl.melonstudios.create.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import nl.melonstudios.create.extensions.IExtensionBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Block.class)
public abstract class MixinBlock implements IExtensionBlock {
    @Shadow
    public abstract boolean isStickyBlock(IBlockState state);

    /*
    @Override
    public boolean create$isSideSticky(IBlockState state, EnumFacing side) {
        return this.isStickyBlock(state);
    }

    @Override
    public void create$addStickyLocations(World world, BlockPos pos, IBlockState state, List<BlockPos> positions) {
        for (EnumFacing side : EnumFacing.VALUES) {
            if (this.create$isSideSticky(state, side)) {
                positions.add(pos.offset(side));
            }
        }
    }
    */
}
