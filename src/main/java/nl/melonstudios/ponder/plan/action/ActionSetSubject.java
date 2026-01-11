package nl.melonstudios.ponder.plan.action;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

@SideOnly(Side.CLIENT)
public class ActionSetSubject implements IPonderAction {
    private final String subject;

    public ActionSetSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public void accept(WorldPonder ponder) {
        ponder.title = this.subject;
    }
}
