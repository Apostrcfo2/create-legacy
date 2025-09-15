package nl.melonstudios.create.tileentity;

import com.melonstudios.melonlib.misc.StackUtil;
import net.minecraft.item.ItemStack;

public class TileEntityMillstone extends TileEntityKinetic {
    private ItemStack input = ItemStack.EMPTY;
    private final ItemStack[] output = new ItemStack[] {
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
    };

    public int timer;
    private String lastMillingRecipe = null;

    @Override
    public void tick() {
        super.tick();

        if (this.getSpeed() == 0) return;
        for (ItemStack stack : this.output) {
            if (stack.getCount() == stack.getMaxStackSize()) return;
        }

        if (this.input.isEmpty()) return;

        this.sync();
    }

    @Override
    public void destroy() {
        super.destroy();
        StackUtil.dropItemsAt(this.world, this.pos, this.input);
    }
}
