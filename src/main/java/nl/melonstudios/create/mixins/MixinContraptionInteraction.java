package nl.melonstudios.create.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import nl.melonstudios.create.entity.EntityContraptionBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mixin(Entity.class)
public class MixinContraptionInteraction {
    @Shadow
    public World world;

    @Unique
    private Stream<EntityContraptionBase> create$getIntersectingContraptionsStream() {
        return this.world.getEntities(EntityContraptionBase.class,
                (entityContraptionBase -> entityContraptionBase.attachedContraption() != null))
                .stream()
                .filter(cEntity -> cEntity != null && cEntity.getEntityBoundingBox()
                        .intersects(((Entity)(Object)this).getEntityBoundingBox()));
    }

    @Unique
    private Set<EntityContraptionBase> create$getIntersectingContraptions() {
        return this.create$getIntersectingContraptionsStream().collect(Collectors.toSet());
    }
}
