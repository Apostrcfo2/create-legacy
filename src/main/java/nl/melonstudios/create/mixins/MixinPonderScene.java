package nl.melonstudios.create.mixins;

import nl.melonstudios.create.extensions.IExtensionPonderScene;
import nl.melonstudios.create.ponder.PonderContraption;
import nl.melonstudios.ponder.scene.PonderScene;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(PonderScene.class)
public class MixinPonderScene implements IExtensionPonderScene {
    @Unique
    private final List<PonderContraption> create$ponderContraptions = new ArrayList<>();
    @Override
    public List<PonderContraption> create$getPonderContraptions() {
        return this.create$ponderContraptions;
    }
}
