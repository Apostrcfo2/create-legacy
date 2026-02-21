package nl.melonstudios.create.command;

import net.minecraft.command.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import nl.melonstudios.create.entity.EntityContraptionBase;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.kinetics.contraption.StickinessPropagator;
import nl.melonstudios.create.tileentity.TileEntityKinetic;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class CommandCreate extends CommandBase {
    @Override
    public String getName() {
        return "create";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/create <command> [<args...>]";
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
            } else throw new EntityNotFoundException("A player is required to put the goggles on");
        } else if ("tryFixKinetics".equals(type)) {
            for (WorldServer world : server.worlds) {
                int dimension = world.provider.getDimension();
                int teCounter = 0;
                for (TileEntity te : world.loadedTileEntityList) {
                    if (te instanceof TileEntityKinetic) {
                        teCounter++;
                        TileEntityKinetic kinetic = (TileEntityKinetic) te;
                        kinetic.calculateCapacity();
                        kinetic.calculateImpact();
                        kinetic.networkDirty = true;
                        kinetic.updateSpeed = true;
                    }
                }
                sender.sendMessage(new TextComponentString("Tried fixing " + teCounter + " kinetic tile entities in dimension " + dimension));
            }
        } else if ("testGlue".equals(type)) {
            BlockPos pos = parseBlockPos(sender, args, 1, true);
            Set<BlockPos> positions = new HashSet<>();
            Set<EntityGlue> glues = new HashSet<>();
            AtomicBoolean failed = new AtomicBoolean(false);
            StickinessPropagator.propagateStickiness(sender.getEntityWorld(), pos, 4096, positions, glues, failed);
            if (failed.get()) {
                sender.sendMessage(new TextComponentString("Glued area too large or has unmovable blocks"));
            } else {
                sender.sendMessage(new TextComponentString("Connected blocks: " + positions.size()));
                sender.sendMessage(new TextComponentString("Connected glue entities: " + glues.size()));
            }
        } else if ("listContraptions".equals(type)) {
            World world = sender.getEntityWorld();
            List<EntityContraptionBase> contraptionBases = world.getEntities(EntityContraptionBearing.class, Entity::isEntityAlive);
            sender.sendMessage(new TextComponentString("Loaded contraptions in this world:"));
            for (EntityContraptionBase con : contraptionBases) {
                sender.sendMessage(new TextComponentString("> " + con.getClass().getSimpleName() + " at " + con.getPositionVector()));
            }
        } else throw new WrongUsageException("Invalid command argument '" + type + "'!");
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "goggles", "tryFixKinetics", "testGlue", "listContraptions");
        }
        if (args.length == 2) {
            if ("testGlue".equals(args[0])) {
                if (targetPos == null) {
                    return getListOfStringsMatchingLastWord(args, "~ ~ ~");
                } else {
                    return getListOfStringsMatchingLastWord(args,
                            String.format("%s %s %s", targetPos.getX(), targetPos.getY(), targetPos.getZ())
                    );
                }
            }
        }
        return Collections.emptyList();
    }
}
