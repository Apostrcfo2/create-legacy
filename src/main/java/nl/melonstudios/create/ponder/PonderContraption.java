package nl.melonstudios.create.ponder;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;

import java.util.function.Consumer;

@SideOnly(Side.CLIENT)
public class PonderContraption {
    public float x, y, z;
    public float param1, param2;
    public final String name;
    public final Contraption contraption;
    public final Type type;
    private final Consumer<PonderContraption> onTick;

    public PonderContraption(String name, Contraption contraption, Type type, Consumer<PonderContraption> onTick) {
        this.name = name;
        this.contraption = contraption;
        this.type = type;
        this.onTick = onTick;
    }

    public void applyTransforms() {
        GlStateManager.translate(this.x+0.5F, this.y+0.5F, this.z+0.5F);
        switch (this.type) {
            case ROTATE_X:
                GlStateManager.rotate(this.param1, 1.0F, 0.0F, 0.0F);
                break;
            case ROTATE_Y:
                GlStateManager.rotate(this.param1, 0.0F, 1.0F, 0.0F);
                break;
            case ROTATE_Z:
                GlStateManager.rotate(this.param1, 0.0F, 0.0F, 1.0F);
                break;
            case FREE:
                GlStateManager.rotate(this.param1, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(this.param2, 1.0F, 0.0F, 0.0F);
                break;
        }
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
    }
    public void render(BlockRenderLayer layer) {
        GlStateManager.callList(ContraptionRendering.getList(this.contraption)[layer.ordinal()]);
    }

    public void tick() {
        if (this.onTick != null) {
            this.onTick.accept(this);
        }
    }

    public enum Type {
        STATIC,
        ROTATE_X(EnumFacing.Axis.X),
        ROTATE_Y(EnumFacing.Axis.Y),
        ROTATE_Z(EnumFacing.Axis.Z),
        FREE;

        public final boolean isBearing;
        public final EnumFacing.Axis bearingAxis;
        Type(EnumFacing.Axis bearingAxis) {
            this.isBearing = bearingAxis != null;
            this.bearingAxis = bearingAxis;
        }
        Type() {
            this(null);
        }
    }
}
