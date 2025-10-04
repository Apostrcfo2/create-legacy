package nl.melonstudios.create.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;
import nl.melonstudios.create.extensions.IExtensionWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.*;

@Mixin(World.class)
public class MixinWorld implements IExtensionWorld {
    @Unique
    private final Set<ITileEntityWithContraption> create$contraptionTileEntities = new HashSet<>();

    @Override
    public Set<ITileEntityWithContraption> create$getContraptionTileEntities() {
        return this.create$contraptionTileEntities;
    }

    @Inject(method = "addTileEntity", at = @At("RETURN"))
    public void addTileEntity(TileEntity tile, CallbackInfoReturnable<Boolean> cir) {
        if (tile instanceof ITileEntityWithContraption && cir.getReturnValue()) {
            this.create$contraptionTileEntities.add((ITileEntityWithContraption) tile);
        }
    }

    @Inject(method = "removeTileEntity", at = @At("RETURN"))
    public void removeTileEntity(BlockPos pos, CallbackInfo ci) {
        this.create$contraptionTileEntities.removeIf((tile) -> ((TileEntity)tile).getPos().equals(pos));
    }
}
