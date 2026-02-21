package nl.melonstudios.create.mixins;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.event.ForgeEventFactory;
import nl.melonstudios.create.util.interfaces.IExcludeAttachingCapabilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ForgeEventFactory.class)
public class MixinForgeEventFactory {
    //Fix "Incompatibility with Deployer and DynamicSurrounding"
    //May need performance testing
    @Inject(method = "gatherCapabilities(Lnet/minecraft/entity/Entity;)Lnet/minecraftforge/common/capabilities/CapabilityDispatcher;", at = @At("HEAD"), cancellable = true, remap = false)
    private static void gatherCapabilities(Entity entity, CallbackInfoReturnable<CapabilityDispatcher> cir) {
        if (entity instanceof IExcludeAttachingCapabilities) cir.setReturnValue(null);
    }
}
