package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.util.BlockRenderLayer;
import nl.melonstudios.create.extensions.IExtensionChunkRenderContainer;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.kinetics.contraption.RenderContraption;
import nl.melonstudios.create.util.PerFrameDebugInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VboRenderList.class)
public class MixinVboRenderList {
    @Inject(method = "renderChunkLayer", at = @At("HEAD"))
    public void renderChunkLayer(BlockRenderLayer layer, CallbackInfo ci) {
        Minecraft.getMinecraft().mcProfiler.startSection("contraptions");
        for (RenderContraption contraption : ContraptionRendering.getRenderContraptions()) {
            if (ContraptionRendering.available(contraption.contraption)) {
                GlStateManager.pushMatrix();
                ((IExtensionChunkRenderContainer) this).create$preRenderContraption(contraption);
                GlStateManager.callList(ContraptionRendering.getListNoCreate(contraption.contraption)[layer.ordinal()]);
                GlStateManager.popMatrix();
                PerFrameDebugInfo.contraptionsRendered[layer.ordinal()]++;
            } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
        }
        if (layer == BlockRenderLayer.CUTOUT) {
            ContraptionRendering.clearRenderContraptions();
        }
        Minecraft.getMinecraft().mcProfiler.endSection();
    }
}
