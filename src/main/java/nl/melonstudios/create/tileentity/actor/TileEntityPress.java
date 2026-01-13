package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.recipe.FlatteningRecipe;
import nl.melonstudios.create.recipe.PressingRecipes;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.IDepot;
import nl.melonstudios.create.tileentity.marker.IHaltBeltContents;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class TileEntityPress extends TileEntityKinetic implements IHaltBeltContents {
    @SideOnly(Side.CLIENT)
    public EnumFacing.Axis getRenderAxis() {
        return this.getState().getValue(BlockStateProperties.HORIZONTAL_AXIS);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getSpeed() != 0.0F) {
            IDepot depot = IDepot.get(this.world, this.pos.down(2));
            if (depot != null) {
                ItemStack stack = depot.getPresentedItem();
                FlatteningRecipe recipe = PressingRecipes.instance.getRecipeForInput(stack);
                if (recipe != null) {
                    this.squishParticles(
                            stack,
                            this.pos.getX() + 0.5,
                            this.pos.getY() - 2 + depot.getItemHeight(),
                            this.pos.getZ() + 0.5,
                            depot
                    );
                    depot.decreasePresentedAndAddOutput(recipe.result.copy());
                }
            } else {
                List<EntityItem> entityItems = this.world.getEntitiesWithinAABB(
                        EntityItem.class,
                        new AxisAlignedBB(this.pos.down()),
                        EntityItem::isEntityAlive
                );
                if (!entityItems.isEmpty()) {
                    for (EntityItem entityItem : entityItems) {
                        ItemStack stack = entityItem.getItem();
                        FlatteningRecipe recipe = PressingRecipes.instance.getRecipeForInput(stack);
                        if (recipe != null) {
                            this.squishParticles(stack, entityItem.posX, entityItem.posY, entityItem.posZ, null);
                            stack.shrink(1);
                            if (stack.isEmpty()) entityItem.setDead();
                            else entityItem.setItem(stack);
                            if (!this.world.isRemote) {
                                StackUtil.spawnItemNoVelocity(this.world, entityItem.posX, entityItem.posY, entityItem.posZ, recipe.result.copy());
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private void squishParticles(ItemStack stack, double x, double y, double z, @Nullable IDepot depot) {
        if (!this.world.isRemote) {
            if (depot != null && depot.isWool()) {
                this.world.playSound(null, x, y, z, SoundInit.block_press_activate, SoundCategory.BLOCKS, 0.5F, 1.0F);
                this.world.playSound(null, x, y, z, SoundEvents.BLOCK_CLOTH_FALL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            } else {
                this.world.playSound(null, x, y, z, SoundInit.block_press_activate, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        } else {
            Random rnd = this.world.rand;
            int offset = rnd.nextInt(45);
            for (int i = 0; i < 8; i++) {
                int rot = i * 45 + offset;
                double sin = Math.sin(Math.toRadians(rot)) * 0.2;
                double cos = Math.cos(Math.toRadians(rot)) * 0.2;
                CreateLegacy.proxy.spawnItemFX(this.world, x, y, z, sin, 0.1, cos, stack);
            }
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
    }

    @Override
    public boolean shouldHaltItem(ItemStack stack) {
        return PressingRecipes.instance.getRecipeForInput(stack) != null;
    }
}
