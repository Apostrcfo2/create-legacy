package nl.melonstudios.create.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import nl.melonstudios.create.init.ItemInit;

import javax.annotation.Nullable;
import java.util.List;

public class CommandCreate extends CommandBase {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "test test";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            throw new SyntaxErrorException("commands.generic.syntax");
        }
        Entity entity = sender.getCommandSenderEntity();
        String type = args[0];
        if ("goggles".equals(type)) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                player.inventory.armorInventory.set(3, new ItemStack(ItemInit.GOGGLES));
                sender.sendMessage(new TextComponentString("Set head armor to goggles"));
            } else throw new EntityNotFoundException("test");
        }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
