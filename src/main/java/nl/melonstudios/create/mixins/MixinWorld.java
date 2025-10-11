package nl.melonstudios.create.mixins;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import nl.melonstudios.create.extensions.IExtensionWorld;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.kinetics.contraption.GluedSurfaceSavedData;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Mixin(World.class)
public abstract class MixinWorld implements IExtensionWorld {
    @Shadow
    @Nullable
    public abstract MapStorage getMapStorage();

    @Unique
    private final Set<ITileEntityWithContraption> create$contraptionTileEntities = new HashSet<>();

    @Override
    public Set<ITileEntityWithContraption> create$getContraptionTileEntities() {
        return this.create$contraptionTileEntities;
    }
    @Override
    public Set<GluedSurface> create$getGluedSurfaces() {
        MapStorage storage = this.getMapStorage();
        if (storage == null) return Collections.emptySet();
        WorldSavedData data = storage.getOrLoadData(GluedSurfaceSavedData.class, "create_glued_surfaces");
        if (data instanceof GluedSurfaceSavedData) {
            GluedSurfaceSavedData surfaces = (GluedSurfaceSavedData) data;
            return surfaces.gluedSurfaces;
        } else return Collections.emptySet();
    }
    @Override
    public boolean create$isSurfaceGlued(GluedSurface surface) {
        MapStorage storage = this.getMapStorage();
        if (storage == null) return false;
        WorldSavedData data = storage.getOrLoadData(GluedSurfaceSavedData.class, "create_glued_surfaces");
        if (data instanceof GluedSurfaceSavedData) {
            GluedSurfaceSavedData surfaces = (GluedSurfaceSavedData) data;
            return surfaces.gluedSurfaces.contains(surface);
        } else return false;
    }
    @Override
    public boolean create$addGluedSurface(GluedSurface surface) {
        MapStorage storage = this.getMapStorage();
        if (storage == null) return false;
        WorldSavedData data = storage.getOrLoadData(GluedSurfaceSavedData.class, "create_glued_surfaces");
        if (data instanceof GluedSurfaceSavedData) {
            GluedSurfaceSavedData surfaces = (GluedSurfaceSavedData) data;
            if (surfaces.gluedSurfaces.add(surface)) {
                surfaces.markDirty();
                return true;
            } else return false;
        } else return false;
    }
    @Override
    public boolean create$removeGluedSurface(GluedSurface surface) {
        MapStorage storage = this.getMapStorage();
        if (storage == null) return false;
        WorldSavedData data = storage.getOrLoadData(GluedSurfaceSavedData.class, "create_glued_surfaces");
        if (data instanceof GluedSurfaceSavedData) {
            GluedSurfaceSavedData surfaces = (GluedSurfaceSavedData) data;
            if (surfaces.gluedSurfaces.remove(surface)) {
                surfaces.markDirty();
                return true;
            } else return false;
        } else return false;
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
