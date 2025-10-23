package nl.melonstudios.create.tileentity.actor;

import com.melonstudios.melonlib.misc.BlockStateProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.recipe.FlatteningRecipe;
import nl.melonstudios.create.recipe.PressingRecipes;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.tileentity.marker.IDepot;
import nl.melonstudios.create.tileentity.marker.IHaltBeltContents;

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
                    depot.decreasePresentedAndAddOutput(recipe.result.copy());
                }
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
    public boolean haltItem(ItemStack stack) {
        return PressingRecipes.instance.getRecipeForInput(stack) != null;
    }
}
