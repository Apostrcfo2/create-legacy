package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderList;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeHooks;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityContraptionBase;
import nl.melonstudios.create.extensions.IExtensionChunkRenderContainer;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.kinetics.contraption.RenderContraption;
import nl.melonstudios.create.util.PerFrameDebugInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Objects;

@Mixin(RenderList.class)
public class MixinRenderList {
    @Inject(method = "renderChunkLayer", at = @At("HEAD"))
    public void renderChunkLayer(BlockRenderLayer layer, CallbackInfo ci) {
        Minecraft.getMinecraft().mcProfiler.startSection("contraptions");

        if (layer == BlockRenderLayer.SOLID) ContraptionRendering.collectContraptions(Minecraft.getMinecraft().world);

        List<EntityContraptionBase> entities = ContraptionRendering.getCollectedContraptions();
        synchronized (entities) {
            if (!entities.isEmpty()) {
                GlStateManager.pushMatrix();

                ((IExtensionChunkRenderContainer)this).create$resetPositionToZero();
                float pt = ContraptionRendering.pt();

                for (EntityContraptionBase entity : entities) {
                    Contraption contraption = entity.attachedContraption();

                    if (contraption != null && ContraptionRendering.available(contraption)) {
                        int[] list = ContraptionRendering.getListNoCreate(contraption);
                        if (list != null) {
                            GlStateManager.pushMatrix();

                            entity.applyRenderTransforms(pt);
                            GlStateManager.callList(list[layer.ordinal()]);

                            GlStateManager.popMatrix();
                            PerFrameDebugInfo.contraptionsRendered[layer.ordinal()]++;
                        } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
                    } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
                }
                GlStateManager.popMatrix();
            }
        }

        Minecraft.getMinecraft().mcProfiler.endSection();
    }
}
