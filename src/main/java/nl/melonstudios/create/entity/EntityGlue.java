package nl.melonstudios.create.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;

public class EntityGlue extends Entity implements IEntityAdditionalSpawnData {
    private GluedSurface surface;
    private boolean wasCovered = false;

    public EntityGlue(World worldIn) {
        super(worldIn);

        this.setSize(1.0F, 1.0F);
    }

    public EntityGlue(World world, GluedSurface surface) {
        this(world);

        this.setPosition(surface.pos.getX() + 0.5F, surface.pos.getY(), surface.pos.getZ() + 0.5F);
        this.surface = surface;
    }

    public GluedSurface getSurface() {
        return this.surface;
    }

    @Override
    public void onUpdate() {
        this.setFire(0);

        if (this.world.getTotalWorldTime() % 10 == 0) {
            this.validateGlueness();
        }
    }

    private void validateGlueness() {
        BlockPos otherPos = this.surface.pos.offset(this.surface.side);
        if (this.world.getBlockState(this.surface.pos).getBlock().isReplaceable(this.world, this.surface.pos)) {
            if (this.world.getBlockState(otherPos).getBlock().isReplaceable(this.world, otherPos)) {
                this.setDead();
                return;
            }
        }

        if (this.world.isRemote) {
            boolean b = false;
            if (!this.world.getBlockState(this.surface.pos).getBlock().isReplaceable(this.world, this.surface.pos)) {
                if (!this.world.getBlockState(otherPos).getBlock().isReplaceable(this.world, otherPos)) {
                    if (!this.wasCovered) {
                        this.spawnTheSlimes();
                        this.wasCovered = true;
                    }
                    b = true;
                }
            }
            if (!b) this.wasCovered = false;
        }
    }

    @Override
    protected void entityInit() {

    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound) {
        BlockPos pos = BlockPos.fromLong(compound.getLong("gluedPos"));
        EnumFacing side = EnumFacing.VALUES[Byte.toUnsignedInt(compound.getByte("gluedSide"))];

        this.surface = new GluedSurface(pos, side);

        this.wasCovered = compound.getBoolean("wasCovered");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound) {
        compound.setLong("gluedPos", this.surface.pos.toLong());
        compound.setByte("gluedSide", (byte)this.surface.side.getIndex());

        compound.setBoolean("wasCovered", this.wasCovered);
    }

    @Override
    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeLong(this.surface.pos.toLong());
        buffer.writeByte(this.surface.side.getIndex());
        buffer.writeBoolean(this.wasCovered);
    }

    @Override
    public void readSpawnData(ByteBuf additionalData) {
        this.surface = new GluedSurface(
                BlockPos.fromLong(additionalData.readLong()),
                EnumFacing.VALUES[additionalData.readByte()]
        );
        this.wasCovered = additionalData.readBoolean();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender() {
        BlockPos otherPos = this.surface.pos.offset(this.surface.side);
        return Math.max(this.world.getCombinedLight(this.surface.pos, 0), this.world.getCombinedLight(otherPos, 0));
    }

    @Override
    public void setDead() {
        super.setDead();

        if (this.world.isRemote) {
            this.spawnTheSlimes();
        }
    }

    private void spawnTheSlimes() {
        int slimeID = Item.getIdFromItem(Items.SLIME_BALL);
        switch (this.surface.side.getAxis()) {
            case X:
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 4; y++) {
                        CreateLegacy.proxy.spawnItemFX(
                                this.world,
                                this.surface.pos.getX() + 1,
                                this.surface.pos.getY() + (x * 0.25),
                                this.surface.pos.getZ() + (y * 0.25),
                                this.random(), this.random(), this.random(),
                                slimeID, 0
                        );
                    }
                }
                break;
            case Y:
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 4; y++) {
                        CreateLegacy.proxy.spawnItemFX(
                                this.world,
                                this.surface.pos.getX() + (x * 0.25),
                                this.surface.pos.getY() + 1,
                                this.surface.pos.getZ() + (y * 0.25),
                                this.random(), this.random(), this.random(),
                                slimeID, 0
                        );
                    }
                }
                break;
            case Z:
                for (int x = 0; x < 4; x++) {
                    for (int y = 0; y < 4; y++) {
                        CreateLegacy.proxy.spawnItemFX(
                                this.world,
                                this.surface.pos.getX() + (x * 0.25),
                                this.surface.pos.getY() + (y * 0.25),
                                this.surface.pos.getZ() + 1,
                                this.random(), this.random(), this.random(),
                                slimeID, 0
                        );
                    }
                }
                break;
        }
    }
    private double random() {
        return this.world.rand.nextDouble() * 0.2 - 0.1;
    }
}
