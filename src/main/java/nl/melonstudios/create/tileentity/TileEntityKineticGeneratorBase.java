package nl.melonstudios.create.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.kinetics.KineticNetwork;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.io.IOException;

public abstract class TileEntityKineticGeneratorBase extends TileEntityKinetic {
    public boolean reactivateSource;

    public TileEntityKineticGeneratorBase() {}

    protected void notifyStressCapacityChange(float capacity) {
        this.getOrCreateNetwork().updateCapacityFor(this, capacity);
    }

    @Override
    public void removeSource() {
        if (this.hasSource() && this.isSource()) this.reactivateSource = true;
        super.removeSource();
    }

    @Override
    public void setSource(BlockPos source) {
        super.setSource(source);
        TileEntity te = this.world.getTileEntity(source);
        if (!(te instanceof TileEntityKinetic)) return;
        if (this.reactivateSource && Math.abs(((TileEntityKinetic)te).getSpeed()) >= Math.abs(this.getGeneratedSpeed()))
            this.reactivateSource = false;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.reactivateSource && !this.isVirtual()) {
            this.updateGeneratedRotation();
            this.reactivateSource = false;
        }
    }

    public void updateGeneratedRotation() {
        final float speed = this.getGeneratedSpeed();
        final float lastSpeed = this.speed;

        if (this.world == null || this.world.isRemote) return;

        if (lastSpeed != speed) {
            if (!this.hasSource()) {
                //TODO: particles
            }
            this.applyNewSpeed(lastSpeed, speed);
        }

        if (this.hasNetwork() && speed != 0.0F) {
            KineticNetwork network = this.getOrCreateNetwork();
            notifyStressCapacityChange(this.calculateCapacity());
            getOrCreateNetwork().updateImpactFor(this, this.calculateImpact());
            network.updateStress();
        }

        this.onSpeedChanged(lastSpeed);
        this.sync();
    }

    public void applyNewSpeed(float lastSpeed, float speed) {
        if (speed == 0.0F) {
            if (this.hasSource()) {
                this.notifyStressCapacityChange(0.0F);
                this.getOrCreateNetwork().updateImpactFor(this, this.calculateImpact());
                return;
            }
            this.detachKinetics();
            this.speed = 0.0F;
            this.setNetwork(null);
            return;
        }

        //Now turning - create a network
        if (lastSpeed == 0.0F) {
            this.speed = speed;
            this.setNetwork(this.pos.toLong());
            this.attachKinetics();
            return;
        }

        if (this.hasSource()) {
            if (Math.abs(lastSpeed) >= Math.abs(speed)) {
                if (Math.signum(lastSpeed) != Math.signum(speed)) this.world.destroyBlock(this.pos, true);
                return;
            }

            this.detachKinetics();
            this.speed = speed;
            this.source = null;
            this.setNetwork(this.pos.toLong());
            this.attachKinetics();
            return;
        }

        this.detachKinetics();
        this.speed = speed;
        this.attachKinetics();
    }

    @OverridingMethodsMustInvokeSuper
    @Override
    public void readPacket(ByteBuf buf) throws IOException {
        super.readPacket(buf);

        this.syncNextTick();
    }
}
