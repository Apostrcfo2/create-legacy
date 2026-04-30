package nl.melonstudios.create.extensions;

import nl.melonstudios.create.kinetics.contraption.RenderContraption;

public interface IExtensionChunkRenderContainer {
    void create$preRenderContraption(RenderContraption renderContraption);

    void create$resetPositionToZero();
}
