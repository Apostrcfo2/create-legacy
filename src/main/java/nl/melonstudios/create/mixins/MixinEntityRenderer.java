package nl.melonstudios.create.mixins;

import net.minecraft.client.renderer.EntityRenderer;
import nl.melonstudios.create.extensions.IExtensionEntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityRenderer.class)
public class MixinEntityRenderer implements IExtensionEntityRenderer {
    @Shadow
    private int rendererUpdateCount;

    @Override
    public int create$getRendererUpdateCount() {
        return this.rendererUpdateCount;
    }
}
