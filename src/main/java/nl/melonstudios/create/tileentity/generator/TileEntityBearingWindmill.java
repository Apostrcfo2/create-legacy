package nl.melonstudios.create.tileentity.generator;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.item.ItemWrench;
import nl.melonstudios.create.kinetics.KineticNetwork;
import nl.melonstudios.create.kinetics.contraption.Contraption;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.actor.TileEntityBearingBase;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TileEntityBearingWindmill extends TileEntityBearingBase implements ITileEntityWithSubInteractions {
    public boolean reactivateSource;
    private Float generatedSpeed = null;
    public boolean flipped = false;
    private boolean doNotUpdateFlipped = false;

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
        if (this.reactivateSource) {
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

    @Override
    protected boolean useGeneratedSpeedForContraption() {
        return true;
    }

    public boolean tryAssemble() {
        if (this.isAssembled()) {
            return this.disassemble();
        }
        return this.assemble();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);
        nbt.setBoolean("flippedGeneration", this.flipped);
        return nbt;
    }
    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (!this.doNotUpdateFlipped) {
            this.flipped = nbt.getBoolean("flippedGeneration");
        } else this.doNotUpdateFlipped = false;
    }

    @Override
    public NBTTagCompound writePacket() {
        NBTTagCompound nbt = super.writePacket();
        if (this.flipped) nbt.setBoolean("flip", true);
        return nbt;
    }
    @Override
    public void readPacket(NBTTagCompound nbt) {
        super.readPacket(nbt);
        this.flipped = nbt.getBoolean("flip");

        this.syncNextTick();
    }

    @Override
    protected boolean assemble() {
        boolean oldFlipped = this.flipped;
        this.generatedSpeed = null;
        this.reactivateSource = true;
        boolean flag = super.assemble();
        this.flipped = oldFlipped; //Hatelijk wangedrag van de computer
        this.doNotUpdateFlipped = true;
        this.syncNextTick();
        return flag;
    }

    @Override
    public boolean disassemble() {
        boolean oldFlipped = this.flipped;
        this.generatedSpeed = null;
        this.reactivateSource = true;
        boolean flag = super.disassemble();
        this.flipped = oldFlipped; //Hatelijk wangedrag van de computer
        this.doNotUpdateFlipped = true;
        this.syncNextTick();
        return flag;
    }

    @Override
    public float getGeneratedSpeed() {
        if (this.generatedSpeed == null) {
            EntityContraptionBearing attached = this.getAttachedContraption();
            if (attached == null) return 0.0F;
            Contraption contraption = attached.contraption;
            int sailCount = 0;
            for (IBlockState state : contraption.blocks.values()) {
                if (BlockDictionary.isBlockTagged(state, "create:sail")) sailCount++;
            }
            int gen = Math.min(sailCount, 128) / 8;
            this.generatedSpeed = (float) gen * (this.flipped ? -1 : 1);
            this.reactivateSource = true;
            this.updateGeneratedRotation();
        }
        return this.generatedSpeed;
    }

    private final List<SubInteractionBox> subInteractionBoxes = new ArrayList<>();
    private void addInteractions() {
        for (EnumFacing side : EnumFacing.VALUES) {
            if (side.getAxis() != this.getState().getValue(BlockBearingBase.FACING).getAxis()) {
                this.subInteractionBoxes.add(SubInteractionBox.Helper.createCenteredSide(side, 0.375F, this.new Interaction()));
            }
        }
    }
    private class Interaction implements SubInteractionBox.ScrollInteraction {
        @Override
        public boolean scroll(EntityPlayer player, boolean sneaking, ItemStack held, int direction) {
            if (held.getItem() instanceof ItemWrench) {
                flipped = !flipped;
                generatedSpeed = null;
                sync();
                return true;
            }
            return false;
        }
    }

    @Override
    public Collection<SubInteractionBox> getSubInteractionBoxes() {
        return this.subInteractionBoxes;
    }

    @Override
    public void initialize() {
        super.initialize();

        //this.addInteractions();
    }

    @Override
    public void initializeClient() {
        super.initializeClient();

        //this.addInteractions();
    }
}
