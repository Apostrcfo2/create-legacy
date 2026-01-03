package nl.melonstudios.create.tileentity.actor;

public class TileEntityBearing extends TileEntityBearingBase {
    @Override
    public void onSpeedChanged(float lastSpeed) {
        super.onSpeedChanged(lastSpeed);

        if (!this.world.isRemote) {
            if (lastSpeed != 0.0F && this.getSpeed() == 0.0F && this.isAssembled()) {
                this.mightDisassemble = true;
            } else if (lastSpeed == 0.0F && this.getSpeed() != 0.0F && !this.isAssembled()) {
                this.mightAssemble = true;
            }
        }
    }
}
