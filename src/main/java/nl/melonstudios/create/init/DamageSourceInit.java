package nl.melonstudios.create.init;

import net.minecraft.util.DamageSource;

public final class DamageSourceInit {
    public static final DamageSource DRILLING = new DamageSource("drilling");
    public static final DamageSource CUTTING = new DamageSource("cutting");
    public static final DamageSource CRUSHING = new DamageSource("crushing").setDamageIsAbsolute().setDamageBypassesArmor();

    private DamageSourceInit() {
        throw new AssertionError("no");
    }
}
