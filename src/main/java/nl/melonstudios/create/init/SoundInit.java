package nl.melonstudios.create.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public final class SoundInit {
    public static SoundEvent item_sandpaper_used;
    public static SoundEvent block_cog_ambient;
    public static SoundEvent block_press_activate;
    public static SoundEvent item_wrench_used_rotate;
    public static SoundEvent item_wrench_used_dismantle;
    public static SoundEvent block_millstone_ambient;
    public static SoundEvent contraption_assemble;
    public static SoundEvent contraption_assemble_compound;
    public static SoundEvent contraption_disassemble;
    public static void init() {
        item_sandpaper_used = registerSound("item.sandpaper.used");
        block_cog_ambient = registerSound("block.cog.ambient");
        block_press_activate = registerSound("block.press.activate");
        item_wrench_used_rotate = registerSound("item.wrench.used.rotate");
        item_wrench_used_dismantle = registerSound("item.wrench.used.dismantle");
        block_millstone_ambient = registerSound("block.millstone.ambient");
        contraption_assemble = registerSound("contraption.assemble");
        contraption_assemble_compound = registerSound("contraption.assemble.compound");
        contraption_disassemble = registerSound("contraption.disassemble");
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
