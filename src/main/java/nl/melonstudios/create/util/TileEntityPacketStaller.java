package nl.melonstudios.create.util;

import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.CreateLegacy;

import java.util.ArrayList;

@SuppressWarnings({"all"})
public class TileEntityPacketStaller {
    public static final ArrayList<Staller> STALLERS = new ArrayList<>();

    public static void tick() {
        for (Staller staller : STALLERS) {
            staller.handler.onMessage(staller.packet, staller.ctx);
        }
        STALLERS.clear();
    }

    public static void addPacketInbound(IMessage packet, IMessageHandler handler, MessageContext ctx) {
        if (CreateLegacy.proxy.getSide() != Side.CLIENT) return;
    }

    public static class Staller {
        public final IMessage packet;
        public final IMessageHandler handler;
        public final MessageContext ctx;

        public Staller(IMessage packet, IMessageHandler handler, MessageContext ctx) {
            this.packet = packet;
            this.handler = handler;
            this.ctx = ctx;
        }
    }
}
