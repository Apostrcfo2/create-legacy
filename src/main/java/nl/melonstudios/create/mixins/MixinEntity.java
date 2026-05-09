package nl.melonstudios.create.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import nl.melonstudios.create.entity.EntityContraptionPiston;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Entity.class)
public abstract class MixinEntity {
    @Shadow
    public World world;

    @Shadow
    public abstract AxisAlignedBB getEntityBoundingBox();

    //@Inject(method = "onUpdate", at = @At("RETURN"))
    public void pushOutContraption(CallbackInfo ci) {
        Profiler profiler = this.world.profiler;

        profiler.startSection("ContraptionCollision");
        List<EntityContraptionPiston> pistons = this.world.getEntities(EntityContraptionPiston.class,
                e -> e.updatedContraptionBB.intersects(this.getEntityBoundingBox()));

        for (EntityContraptionPiston piston : pistons) {

        }
        profiler.endSection();
    }
}
