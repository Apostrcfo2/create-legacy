package nl.melonstudios.create.extensions;

import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.kinetics.contraption.ITileEntityWithContraption;

import java.util.Set;

public interface IExtensionWorld {
    Set<ITileEntityWithContraption> create$getContraptionTileEntities();
    Set<GluedSurface> create$getGluedSurfaces();

    boolean create$isSurfaceGlued(GluedSurface surface);
    boolean create$addGluedSurface(GluedSurface surface);
    boolean create$removeGluedSurface(GluedSurface surface);
}
