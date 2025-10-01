package nl.melonstudios.create.mixins;

import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.PixelFormat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Final
    @Shadow
    private static Logger LOGGER;

    @Shadow
    private boolean fullscreen;

    /**
     * @author siepert
     * @reason testing
     */
    @Overwrite
    private void createDisplay() throws LWJGLException {
        Display.setResizable(true);
        Display.setTitle("if you see this I forgot to remove the test mixin");
        try {
            Display.create((new PixelFormat()).withDepthBits(24));
        } catch (LWJGLException lwjglexception) {
            LOGGER.error("Couldn't set pixel format", lwjglexception);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
            }
            if (this.fullscreen) {
                this.updateDisplayMode();
            }
            Display.create();
        }
    }

    @Shadow
    private void updateDisplayMode() {}
}
