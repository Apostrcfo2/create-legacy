package nl.melonstudios.create.ponder;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.extensions.IExtensionPonderScene;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Function;

@SideOnly(Side.CLIENT)
public class ActionAddContraption implements IPonderAction {
    private final Function<WorldPonder, PonderContraption> function;

    public ActionAddContraption(Function<WorldPonder, PonderContraption> function) {
        this.function = function;
    }

    @Override
    public void accept(WorldPonder ponder) {
        PonderContraption contraption = this.function.apply(ponder);
        ((IExtensionPonderScene)ponder.scene).create$getPonderContraptions().add(contraption);
    }

    @Override
    public boolean requiresMeshUpdate() {
        return true;
    }
}
