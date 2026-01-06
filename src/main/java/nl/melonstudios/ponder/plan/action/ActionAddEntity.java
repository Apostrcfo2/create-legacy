package nl.melonstudios.ponder.plan.action;

import net.minecraft.entity.Entity;
import nl.melonstudios.ponder.IVirtualizable;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.EnumEntityPonder;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.function.Function;

public class ActionAddEntity implements IPonderAction {
    private final Function<WorldPonder, Entity> sup;
    private final EnumEntityPonder type;

    public ActionAddEntity(EnumEntityPonder type, Function<WorldPonder, Entity> sup) {
        this.type = type;
        this.sup = sup;
    }

    @Override
    public void accept(WorldPonder ponder) {
        Entity entity = this.sup.apply(ponder);
        if (entity instanceof IVirtualizable) {
            ((IVirtualizable)entity).markAsVirtual();
        }
        switch (this.type) {
            case NORMAL:
                ponder.scene.entityList.add(entity);
                break;
            case RENDER_ONLY:
                ponder.scene.renderOnlyEntityList.add(entity);
                break;
            case NON_TICKING_RENDER_ONLY:
                ponder.scene.nonTickingRenderOnlyEntityList.add(entity);
                break;
        }
    }
}
