package nl.melonstudios.ponder.plan.action;

import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.scene.PonderScene;
import nl.melonstudios.ponder.world.WorldPonder;

public class ActionSetScene implements IPonderAction {
    private final String key;

    public ActionSetScene(String name) {
        this.key = name;
    }

    @Override
    public void accept(WorldPonder ponder) {
        if (ponder.scenes.containsKey(this.key)) {
            ponder.scene = ponder.scenes.get(this.key);
        } else {
            PonderScene newScene = ponder.container.sceneProviders.get(this.key).get();
            ponder.scenes.put(this.key, newScene);
            ponder.scene = newScene;
        }
    }

    @Override
    public boolean requiresMeshUpdate() {
        return true;
    }
}
