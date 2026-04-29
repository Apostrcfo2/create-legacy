package nl.melonstudios.create.mixins;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.MinecraftForgeClient;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderGlobal.class)
public class MixinRenderGlobal {
    @Inject(method = "renderEntities", at = @At("HEAD"))
    public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks, CallbackInfo ci) {
        if (MinecraftForgeClient.getRenderPass() == 0) {
            ContraptionRendering.clearRenderContraptions();
        }
    }
}
