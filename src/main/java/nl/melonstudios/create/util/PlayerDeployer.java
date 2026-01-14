package nl.melonstudios.create.util;

import com.mojang.authlib.GameProfile;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import nl.melonstudios.create.tileentity.actor.TileEntityDeployer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class PlayerDeployer extends EntityPlayer {
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
}
