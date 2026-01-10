package nl.melonstudios.create.item;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.CreateLegacy;

import javax.annotation.ParametersAreNonnullByDefault;
import java.io.File;
import java.io.IOException;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemSceneWand extends Item {
    public ItemSceneWand() {
        super();

        this.setRegistryName("scene_wand");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && CreateLegacy.proxy.getSide() == Side.CLIENT) {
            NBTTagCompound nbt = player.getHeldItem(hand).getOrCreateSubCompound("SceneWand");
            if (nbt.hasKey("FirstPos", Constants.NBT.TAG_INT_ARRAY)) {
                if (nbt.hasKey("SecondPos", Constants.NBT.TAG_INT_ARRAY)) {
                    int[] array = nbt.getIntArray("FirstPos");
                    int[] array2 = nbt.getIntArray("SecondPos");
                    BlockPos firstPos = new BlockPos(array[0], array[1], array[2]);
                    BlockPos secondPos = new BlockPos(array2[0], array2[1], array2[2]);
                    File downloads = new File(System.getProperty("user.home") + "/Downloads");
                    String fileName = "scene-" + System.nanoTime() + ".nbt";
                    File result = new File(downloads, fileName);
                    try {
                        if (result.exists() && !result.delete()) {
                            player.sendStatusMessage(new TextComponentString("Could not delete duplicate scene file, expect corruption"), true);
                        }
                        if (result.createNewFile()) {
                            player.sendStatusMessage(new TextComponentString("Saving scene..."), true);
                            NBTTagCompound scene = new NBTTagCompound();
                            NBTTagList blocks = new NBTTagList();
                            scene.setTag("Blocks", blocks);
                            BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
                            int minX = Math.min(firstPos.getX(), secondPos.getX());
                            int minY = Math.min(firstPos.getY(), secondPos.getY());
                            int minZ = Math.min(firstPos.getZ(), secondPos.getZ());
                            int maxX = Math.max(firstPos.getX(), secondPos.getX());
                            int maxY = Math.max(firstPos.getY(), secondPos.getY());
                            int maxZ = Math.max(firstPos.getZ(), secondPos.getZ());
                            for (int x = minX; x <= maxX; x++) {
                                for (int y = minY; y <= maxY; y++) {
                                    for (int z = minZ; z <= maxZ; z++) {
                                        mutable.setPos(x, y, z);
                                        IBlockState state = worldIn.getBlockState(mutable);
                                        if (!state.getBlock().isAir(state, worldIn, mutable)) {
                                            NBTTagCompound block = new NBTTagCompound();
                                            block.setTag("Pos", NBTUtil.createPosTag(mutable.add(-minX, -minY, -minZ)));
                                            block.setTag("State", NBTUtil.writeBlockState(new NBTTagCompound(), state));
                                            blocks.appendTag(block);
                                        }
                                    }
                                }
                            }
                            CompressedStreamTools.write(scene, result);
                            player.sendStatusMessage(new TextComponentString("Saved scene to downloads folder / " + fileName), false);
                        } else throw new IOException("Failed to create scene file, try again");
                    } catch (IOException e) {
                        player.sendStatusMessage(new TextComponentString("Failed to save scene: " + e.getClass().getName() + ": " + e.getLocalizedMessage()), false);
                    }
                    nbt.removeTag("FirstPos");
                    nbt.removeTag("SecondPos");
                } else {
                    nbt.setIntArray("SecondPos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                    player.sendStatusMessage(new TextComponentString("Set second pos"), false);
                }
            } else {
                nbt.setIntArray("FirstPos", new int[]{pos.getX(), pos.getY(), pos.getZ()});
                player.sendStatusMessage(new TextComponentString("Set first pos"), false);
            }
        }
        return EnumActionResult.SUCCESS;
    }
}