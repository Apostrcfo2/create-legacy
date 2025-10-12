package nl.melonstudios.create.mixins;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import nl.melonstudios.create.extensions.IExtensionTileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntity.class)
public class MixinTileEntity implements IExtensionTileEntity {
    @Shadow
    protected Block blockType;

    @Shadow
    private int blockMetadata;

    @Override
    public void create$setState(IBlockState state) {
        this.blockType = state.getBlock();
        this.blockMetadata = this.blockType.getMetaFromState(state);
    }
}
