package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.entity.EntityContraptionBase;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {
    @Inject(method = "drawBatch", at = @At("HEAD"), remap = false)
    public void injectContraptionTiles(int pass, CallbackInfo ci) {
        Minecraft.getMinecraft().mcProfiler.startSection("contraptions");
        List<EntityContraptionBase> entities = ContraptionRendering.getCollectedContraptions();

        synchronized (entities) {
            if (!entities.isEmpty()) {
                int oldPass = ForgeHooksClient.getWorldRenderPass();
                ForgeHooksClient.setRenderPass(1);

                RenderHelper.enableStandardItemLighting();
                GlStateManager.pushMatrix();
                GlStateManager.translate(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);
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
    }
}
