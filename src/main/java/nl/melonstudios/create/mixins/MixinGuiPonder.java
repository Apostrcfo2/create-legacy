package nl.melonstudios.create.mixins;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockRenderLayer;
import nl.melonstudios.create.extensions.IExtensionPonderScene;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.ponder.PonderContraption;
import nl.melonstudios.ponder.scene.GuiPonder;
import nl.melonstudios.ponder.world.WorldPonder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiPonder.class)
public class MixinGuiPonder {
    @Shadow
    @Final
    private WorldPonder ponder;

    @Inject(method = "onGuiClosed", at = @At("RETURN"))
    public void onGuiClosed(CallbackInfo ci) {
        for (PonderContraption contraption : ((IExtensionPonderScene)this.ponder.scene).create$getPonderContraptions()) {
            ContraptionRendering.contraptionFinalized(contraption.contraption);
        }
    }

    @Inject(method = "updateScreen", at = @At("RETURN"))
    public void updateScreen(CallbackInfo ci) {
        ((IExtensionPonderScene)this.ponder.scene).create$getPonderContraptions().forEach(PonderContraption::tick);
    }

    @Inject(method = "renderBlocks", at = @At("RETURN"), remap = false)
    public void renderBlocks(BlockRenderLayer layer, CallbackInfo ci) {
        for (PonderContraption contraption : ((IExtensionPonderScene)this.ponder.scene).create$getPonderContraptions()) {
            GlStateManager.pushMatrix();
            contraption.applyTransforms();
            contraption.render(layer);
            GlStateManager.popMatrix();
        }
    }
}
