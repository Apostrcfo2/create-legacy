package nl.melonstudios.create.mixins;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.ResourceLocation;
import nl.melonstudios.create.extensions.IExtensionMobSpawnerBaseLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;

@Mixin(MobSpawnerBaseLogic.class)
public abstract class MixinMobSpawnerBaseLogic implements IExtensionMobSpawnerBaseLogic {
    @Shadow
    @Nullable
    protected abstract ResourceLocation getEntityId();

    @Override
    public Class<? extends Entity> create$getSpawnedEntity() {
        ResourceLocation entityID = this.getEntityId();
        if (entityID == null) return EntityPig.class;
        Class<? extends Entity> clazz = EntityList.getClass(entityID);
        return clazz != null ? clazz : EntityPig.class;
    }
}
