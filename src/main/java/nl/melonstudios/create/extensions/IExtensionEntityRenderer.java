package nl.melonstudios.create.extensions;

import net.minecraft.client.renderer.EntityRenderer;

public interface IExtensionEntityRenderer {
    int create$getRendererUpdateCount();

    static int getRendererUpdateCount(EntityRenderer renderer) {
        return ((IExtensionEntityRenderer)renderer).create$getRendererUpdateCount();
    }
}
