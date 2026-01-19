package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
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

    @Inject(method = "checkGLError", at = @At("HEAD"), cancellable = true)
    private void checkGLError(String message, CallbackInfo ci) {
        if (this.currentScreen instanceof GuiPonder) ci.cancel();
    }
}
