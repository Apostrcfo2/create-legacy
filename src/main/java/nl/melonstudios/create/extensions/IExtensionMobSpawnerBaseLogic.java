package nl.melonstudios.create.extensions;

import net.minecraft.entity.Entity;

public interface IExtensionMobSpawnerBaseLogic {
    Class<? extends Entity> create$getSpawnedEntity();
}
