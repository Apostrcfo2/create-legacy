package nl.melonstudios.create.mixins;

import net.minecraft.client.renderer.ChunkRenderContainer;
import net.minecraft.client.renderer.GlStateManager;
import nl.melonstudios.create.extensions.IExtensionChunkRenderContainer;
import nl.melonstudios.create.kinetics.contraption.RenderContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ChunkRenderContainer.class)
public class MixinChunkRenderContainer implements IExtensionChunkRenderContainer {
    @Shadow
    private double viewEntityX;
    @Shadow
    private double viewEntityY;
    @Shadow
    private double viewEntityZ;

    @Override
    public void create$preRenderContraption(RenderContraption renderContraption) {
        if (renderContraption.preRenderPredicate.getAsBoolean()) {
            GlStateManager.translate(-this.viewEntityX, -this.viewEntityY, -this.viewEntityZ);
            renderContraption.preRenderLogic.run();
        }
    }

    @Override
    public void create$resetPositionToZero() {
        GlStateManager.translate(-this.viewEntityX, -this.viewEntityY, -this.viewEntityZ);
    }
}
