package nl.melonstudios.create.util;

import com.mojang.authlib.GameProfile;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import nl.melonstudios.create.block.actor.BlockDeployer;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;
import nl.melonstudios.create.util.interfaces.IExcludeAttachingCapabilities;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlayerDeployer extends EntityPlayer implements IExcludeAttachingCapabilities {
    private final TileEntityDeployer deployer;
    public PlayerDeployer(TileEntityDeployer deployer) {
        super(deployer.getWorld(), new GameProfile(UUID.randomUUID(), "Deployer-" + Long.toHexString(deployer.getPos().toLong())));
        this.deployer = deployer;
    }

    @Override
    public boolean isSpectator() {
        return false;
    }
    @Override
    public boolean isCreative() {
        return false;
    }

    @Override
    public World getEntityWorld() {
        return this.deployer.getWorld();
    }
    @Override
    public BlockPos getPosition() {
        return this.deployer.getPos();
    }

    @Override
    public Vec3d getPositionEyes(float partialTicks) {
        return this.getPositionVector();
    }

    @Override
    public boolean addItemStackToInventory(ItemStack stack) {
        if (this.deployer.cloggedItem.isEmpty()) {
            this.deployer.cloggedItem = stack;
            this.deployer.sync();
            return true;
        }
        return false;
    }

    @Override
    public EnumFacing getHorizontalFacing() {
        EnumFacing facing = this.deployer.getState().getValue(BlockDeployer.FACING);
        return facing.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : facing;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return false;
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        return null;
    }
}
