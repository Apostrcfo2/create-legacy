package nl.melonstudios.create.network.spacket;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import nl.melonstudios.create.network.CreateLegacyPacketManager;
import nl.melonstudios.create.network.SPacketBase;
import nl.melonstudios.create.tileentity.marker.ITileEntityWithSubInteractions;
import nl.melonstudios.create.util.SubInteractionBox;

import javax.annotation.Nullable;
import java.util.Collection;

public class SPacketScrollInteraction extends SPacketBase {
    @Nullable
    @Override
    public FMLProxyPacket handle(PacketBuffer data, EntityPlayerMP player) {
        BlockPos pos = data.readBlockPos();
        float hitX = data.readFloat();
        float hitY = data.readFloat();
        float hitZ = data.readFloat();
        int direction = data.readByte();
        World world = player.world;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof ITileEntityWithSubInteractions) {
            Collection<SubInteractionBox> boxes = ((ITileEntityWithSubInteractions)te).getSubInteractionBoxes();
            for (SubInteractionBox box : boxes) {
                if (box.isInside(hitX, hitY, hitZ) && box.getInteraction() instanceof SubInteractionBox.ScrollInteraction) {
                    SubInteractionBox.ScrollInteraction interaction = (SubInteractionBox.ScrollInteraction) box.getInteraction();
                    interaction.scroll(player, player.isSneaking(), player.getHeldItem(EnumHand.MAIN_HAND), direction);
                    break;
                }
            }
        }
        return null;
    }

    public FMLProxyPacket create(BlockPos pos, float hitX, float hitY, float hitZ, int direction) {
        PacketBuffer data = this.buf();
        data.writeBlockPos(pos);
        data.writeFloat(hitX);
        data.writeFloat(hitY);
        data.writeFloat(hitZ);
        data.writeByte(direction);
        return new FMLProxyPacket(data, CreateLegacyPacketManager.CHANNEL);
    }
}
