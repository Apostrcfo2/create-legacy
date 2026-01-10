package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import nl.melonstudios.ponder.PonderContainer;
import nl.melonstudios.ponder.PonderRegistry;
import nl.melonstudios.ponder.scene.GuiPonder;
import nl.melonstudios.ponder.world.WorldPonder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public class MixinGuiContainer {
    @Shadow
    private Slot hoveredSlot;

    @Inject(method = "keyTyped", at = @At("HEAD"))
    public void keyTyped(char typedChar, int keyCode, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.hoveredSlot.getHasStack()) {
            ItemStack stack = this.hoveredSlot.getStack();
            if (typedChar == 'w') {
                if (PonderRegistry.hasPonder(stack)) {
                    PonderContainer container = PonderRegistry.getPonder(stack);
                    Minecraft.getMinecraft().displayGuiScreen(
                            new GuiPonder(Minecraft.getMinecraft().currentScreen,
                                    new WorldPonder(container, Minecraft.getMinecraft().mcProfiler)
                            )
                    );
                }
            }
        }
    }
}
