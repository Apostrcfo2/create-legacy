package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.ponder.scene.GuiPonder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Shadow
    @Nullable
    public GuiScreen currentScreen;

    @Shadow
    public WorldClient world;

    @Inject(method = "checkGLError", at = @At("HEAD"), cancellable = true)
    private void checkGLError(String message, CallbackInfo ci) {
        if (this.currentScreen instanceof GuiPonder) ci.cancel();
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    public void cleanupContraptions(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (this.world != null && this.world != worldClientIn) {
            ContraptionRendering.cleanupContraptions(this.world);
        }
    }
}
