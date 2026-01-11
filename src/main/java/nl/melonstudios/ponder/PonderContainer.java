package nl.melonstudios.ponder;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.scene.IPonderSceneProvider;

import java.util.HashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public final class PonderContainer {
    public final Map<String, IPonderSceneProvider> sceneProviders = new HashMap<>();
    public final PonderPlan plan;

    public PonderContainer(PonderPlan plan) {
        this.plan = plan;
    }
    public PonderContainer addSceneProvider(String key, IPonderSceneProvider provider) {
        this.sceneProviders.put(key, provider);
        return this;
    }
}
