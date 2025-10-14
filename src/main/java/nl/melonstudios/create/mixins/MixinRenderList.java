package nl.melonstudios.create.mixins;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.util.BlockRenderLayer;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.extensions.IExtensionChunkRenderContainer;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.kinetics.contraption.RenderContraption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderList.class)
public class MixinRenderList {
    @Inject(method = "renderChunkLayer", at = @At("HEAD"))
    public void renderChunkLayer(BlockRenderLayer layer, CallbackInfo ci) {
        for (RenderContraption contraption : ContraptionRendering.getRenderContraptions()) {
            GlStateManager.pushMatrix();
            ((IExtensionChunkRenderContainer)this).create$preRenderContraption(contraption);
            GlStateManager.callList(ContraptionRendering.getList(contraption.contraption) + layer.ordinal());
            CreateLegacy.logger.debug("layer {} contraption", layer);
            GlStateManager.popMatrix();
        }
        if (layer == BlockRenderLayer.CUTOUT) {
            ContraptionRendering.clearRenderContraptions();
        }
    }
}
