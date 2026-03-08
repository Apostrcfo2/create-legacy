package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.blockdict.BlockDictionary;
import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.melonstudios.create.kinetics.contraption.ContraptionInventory;
import nl.melonstudios.create.kinetics.contraption.IContraptionActor;
import nl.melonstudios.create.kinetics.contraption.accessor.IContraptionAccessor;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.ArrayList;
import java.util.List;

public class TileEntityHarvester extends TileEntity implements IContraptionActor {
    public TileEntityHarvester() {

    }

    public boolean moving = false;
    public float rotationOld = 0.0F;
    public float rotation = 0.0F;
    @Override
    public void setOnContraption(boolean onContraption) {
        this.moving = onContraption;
        this.rotation = this.rotationOld = 0.0F;
    }
    @Override
    public boolean isOnContraption() {
        return this.moving;
    }

    @Override
    public void contraptionTick(IContraptionAccessor contraption, World world, Vector3fc position, BlockPos blockPosition, boolean moved, Vector3fc movement) {
        this.rotationOld = this.rotation;
        this.rotation += movement.length();
        if (moved && !world.isRemote) {
            IBlockState crop = world.getBlockState(blockPosition);
            if (crop.getBlock() instanceof BlockCrops && !BlockDictionary.isBlockTagged(crop, "create:harvesterBlacklist")) {
                BlockCrops crops = (BlockCrops) crop.getBlock();
                if (crops.isMaxAge(crop)) {
                    ContraptionInventory inventory = contraption.getInventory();
                    NonNullList<ItemStack> drops = NonNullList.create();
                    crops.getDrops(drops, world, blockPosition, crop, 0);
                    List<ItemStack> leftovers = new ArrayList<>();
                    for (ItemStack stack : drops) {
                        stack = inventory.insertItem(stack, false);
                        if (!stack.isEmpty()) leftovers.add(stack);
                    }
                    if (!leftovers.isEmpty()) {
                        StackUtil.dropItemsAt(world, blockPosition, leftovers.toArray(new ItemStack[0]));
                    }
                    world.playEvent(2001, blockPosition, BlockCrops.getStateId(crop));
                    world.setBlockState(blockPosition, crops.withAge(0));
                }
            }
        }
    }
}
