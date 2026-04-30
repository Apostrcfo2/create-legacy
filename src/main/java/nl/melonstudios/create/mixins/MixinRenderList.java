package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
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

import java.util.Objects;

@Mixin(RenderList.class)
public class MixinRenderList {
    @Inject(method = "renderChunkLayer", at = @At("HEAD"))
    public void renderChunkLayer(BlockRenderLayer layer, CallbackInfo ci) {
        Minecraft.getMinecraft().mcProfiler.startSection("contraptions");
        synchronized (ContraptionRendering.CONTRAPTIONS_TO_RENDER) {
            if (!ContraptionRendering.CONTRAPTIONS_TO_RENDER.isEmpty()) {
                GlStateManager.pushMatrix();
                ((IExtensionChunkRenderContainer)this).create$resetPositionToZero();
                float pt = ContraptionRendering.pt();
                for (EntityContraptionBase entity : ContraptionRendering.CONTRAPTIONS_TO_RENDER) {
                    Contraption contraption = entity.attachedContraption();
                    if (contraption != null && ContraptionRendering.available(contraption)) {
                        GlStateManager.pushMatrix();
                        entity.applyRenderTransforms(pt);
                        GlStateManager.callList(ContraptionRendering.getListNoCreate(contraption)[layer.ordinal()]);
                        GlStateManager.popMatrix();
                        PerFrameDebugInfo.contraptionsRendered[layer.ordinal()]++;
                    } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
                }
                GlStateManager.popMatrix();
            }
        }
        /*
        for (RenderContraption contraption : ContraptionRendering.getRenderContraptions()) {
            if (ContraptionRendering.available(contraption.contraption)) {
                GlStateManager.pushMatrix();
                ((IExtensionChunkRenderContainer) this).create$preRenderContraption(contraption);
                GlStateManager.callList(ContraptionRendering.getListNoCreate(contraption.contraption)[layer.ordinal()]);
                GlStateManager.popMatrix();
                PerFrameDebugInfo.contraptionsRendered[layer.ordinal()]++;
            } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
        }
        */
        //if (layer == BlockRenderLayer.CUTOUT) {
        //    ContraptionRendering.clearRenderContraptions();
        //}
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void renderChunkLayerTEs(BlockRenderLayer layer, CallbackInfo ci) {
        if (layer == BlockRenderLayer.CUTOUT) {
            Minecraft.getMinecraft().mcProfiler.startSection("contraptions");
            Minecraft.getMinecraft().mcProfiler.startSection("tileentities");
            synchronized (ContraptionRendering.CONTRAPTIONS_TO_RENDER) {
                if (!ContraptionRendering.CONTRAPTIONS_TO_RENDER.isEmpty()) {
                    int oldPass = ForgeHooksClient.getWorldRenderPass();
                    ForgeHooksClient.setRenderPass(1);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.pushMatrix();
                    ((IExtensionChunkRenderContainer)this).create$resetPositionToZero();
                    float pt = ContraptionRendering.pt();
                    for (EntityContraptionBase entity : ContraptionRendering.CONTRAPTIONS_TO_RENDER) {
                        Contraption contraption = entity.attachedContraption();
                        if (contraption != null && !contraption.tileEntities.isEmpty()) {
                            GlStateManager.pushMatrix();
                            entity.applyRenderTransforms(pt);
                            for (TileEntity te : contraption.tileEntities.values()) {
                                if (contraption.blacklistedForRendering.contains(te)) continue;
                                TileEntitySpecialRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer(te);
                                if (tesr != null) {
                                    GlStateManager.pushMatrix();
                                    try {
                                        tesr.render(te, te.getPos().getX(), te.getPos().getY(), te.getPos().getZ(), pt, -1, 1.0F);
                                    } catch (Throwable e) {
                                        CreateLegacy.logger.warn("Exception rendering tile entity", e);
                                        contraption.blacklistedForRendering.add(te);
                                    }
                                    GlStateManager.popMatrix();
                                }
                            }
                            GlStateManager.popMatrix();
                        }
                    }
                    GlStateManager.popMatrix();
                    RenderHelper.disableStandardItemLighting();
                    ForgeHooksClient.setRenderPass(oldPass);
                }
            }
            Minecraft.getMinecraft().mcProfiler.endSection();
            Minecraft.getMinecraft().mcProfiler.endSection();
        }
    }
}
