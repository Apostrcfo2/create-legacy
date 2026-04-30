package nl.melonstudios.create.mixins;

import net.minecraft.client.gui.GuiNewChat;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {
    @Inject(method = "clearChatMessages", at = @At("RETURN"))
    public void removeDanglingContraptions(boolean flag, CallbackInfo ci) {
        if (flag) {
            ContraptionRendering.CONTRAPTIONS_TO_REMOVE.addAll(ContraptionRendering.CONTRAPTIONS_TO_RENDER);
            ContraptionRendering.CONTRAPTIONS_TO_RENDER.clear();
        }
    }
}
