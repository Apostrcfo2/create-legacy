package nl.melonstudios.create.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class SoundInit {
    public static SoundEvent wrenchRemove, wrenchRotate;
    public static void init() {
        wrenchRemove = registerSound("wrench_remove");
        wrenchRotate = registerSound("wrench_rotate");
    }

    private static SoundEvent registerSound(String registry) {
        ResourceLocation rsl = new ResourceLocation("create", registry);
        SoundEvent soundEvent = new SoundEvent(rsl);
        soundEvent.setRegistryName(registry);
        ForgeRegistries.SOUND_EVENTS.register(soundEvent);
        return soundEvent;
    }

    private SoundInit() {
        throw new AssertionError("no");
    }
}
