package nl.melonstudios.ponder.plan;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class PonderPlan {
    public final List<IPonderAction> initializationPlan;
    public final Long2ObjectMap<List<IPonderAction>> timePlan;
    public final long lastPlanTimestamp;

    public PonderPlan(List<IPonderAction> initializationPlan, Long2ObjectMap<List<IPonderAction>> timePlan) {
        this.initializationPlan = initializationPlan;
        this.timePlan = timePlan;
        long time = 0L;
        for (long key : timePlan.keySet()) {
            time = Math.max(time, key);
        }
        this.lastPlanTimestamp = time;
    }

    public static PonderPlan withBuilder(Consumer<PonderPlanBuilder> builder) {
        PonderPlanBuilder ponderPlanBuilder = new PonderPlanBuilder();
        builder.accept(ponderPlanBuilder);
        return ponderPlanBuilder.build();
    }
}
