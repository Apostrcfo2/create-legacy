package nl.melonstudios.create.item;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class ItemNoGravity extends Item {
    @Override
    public boolean onEntityItemUpdate(EntityItem entityItem) {
        World world = entityItem.world;
        NBTTagCompound data = entityItem.getEntityData();

        if (world.isRemote) {
            return false;
        }

        entityItem.setNoGravity(true);
        if (!data.hasKey("JustCreated")) return false;
        this.onCreated(entityItem, data);
        return false;
    }

    protected void onCreated(EntityItem item, NBTTagCompound data) {
        item.lifespan = 6000;
        data.removeTag("JustCreated");
        item.setSilent(true);
    }
}
