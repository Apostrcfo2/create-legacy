package nl.melonstudios.create.ponder;

import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.kinetics.contraption.IContraptionHolder;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.plan.PonderPlanBuilder;
import nl.melonstudios.ponder.plan.action.ActionAddEntity;
import nl.melonstudios.ponder.world.EnumEntityPonder;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CreatePonderPlanBuilder extends PonderPlanBuilder {
    public CreatePonderPlanBuilder(String name) {
        super(name);
    }

    public void setSpeed(BlockPos pos, float speed) {
        this.addAction(new ActionSetKineticSpeed(pos, speed));
    }
    public void setSpeedEnMasse(float speed, Predicate<TileEntityKinetic> filter) {
        this.addAction(new ActionSetKineticSpeedEnMasse(speed, filter));
    }
    public void addGlue(GluedSurface surface) {
        this.addAction(new ActionAddEntity(EnumEntityPonder.NORMAL, (world) -> new EntityGlue(world, surface)));
    }
    public void assembleContraption(String name, BlockPos pos, BlockPos exclude, PonderContraption.Type type, Consumer<PonderContraption> onTick) {
        this.addAction(new ActionAddContraption((world) -> {
            Contraption contraption = Contraption.assemble(new PonderContraptionHolder(world), pos, exclude);
            PonderContraption pContraption = new PonderContraption(name, contraption, type, onTick);
            pContraption.x = pos.getX();
            pContraption.y = pos.getY();
            pContraption.z = pos.getZ();
            return pContraption;
        }));
    }

    public static PonderPlan builder(String name, Consumer<CreatePonderPlanBuilder> builder) {
        CreatePonderPlanBuilder planBuilder = new CreatePonderPlanBuilder(name);
        builder.accept(planBuilder);
        return planBuilder.build();
    }
}
