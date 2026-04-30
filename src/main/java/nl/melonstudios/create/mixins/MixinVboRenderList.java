package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.VboRenderList;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraftforge.client.ForgeHooksClient;
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

@Mixin(VboRenderList.class)
public class MixinVboRenderList {
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
                        GlStateManager.pushMatrix();

                        entity.applyRenderTransforms(pt);
                        int[] list = ContraptionRendering.getListNoCreate(contraption);
                        if (list != null) GlStateManager.callList(list[layer.ordinal()]);

                        GlStateManager.popMatrix();
                        PerFrameDebugInfo.contraptionsRendered[layer.ordinal()]++;
                    } else PerFrameDebugInfo.contraptionsSkipped[layer.ordinal()]++;
                }
                GlStateManager.popMatrix();
            }
        }

        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    @Inject(method = "renderChunkLayer", at = @At("RETURN"))
    public void renderChunkLayerTEs(BlockRenderLayer layer, CallbackInfo ci) {
        if (layer == BlockRenderLayer.CUTOUT) {
            Minecraft.getMinecraft().mcProfiler.startSection("contraptions");
            Minecraft.getMinecraft().mcProfiler.startSection("tileentities");
            List<EntityContraptionBase> entities = ContraptionRendering.getCollectedContraptions();

            synchronized (entities) {
                if (!entities.isEmpty()) {
                    int oldPass = ForgeHooksClient.getWorldRenderPass();
                    ForgeHooksClient.setRenderPass(1);

                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.pushMatrix();
                    ((IExtensionChunkRenderContainer)this).create$resetPositionToZero();
                    float pt = ContraptionRendering.pt();

                    for (EntityContraptionBase entity : entities) {
                        Contraption contraption = entity.attachedContraption();

                        if (contraption != null && !contraption.tileEntities.isEmpty()) {
                            GlStateManager.pushMatrix();
                            entity.applyRenderTransforms(pt);

                            for (TileEntity te : contraption.tileEntities.values()) {
                                if (contraption.blacklistedForRendering.contains(te)) continue;
                                TileEntitySpecialRenderer<TileEntity> tesr = TileEntityRendererDispatcher.instance.getRenderer(te);

                                if (tesr != null) {
                                    GlStateManager.pushMatrix();

                                    int light = contraption.getCombinedLight(te.getPos(), 0);
                                    int j = light % 65536;
                                    int k = light / 65536;
                                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, j, k);

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
