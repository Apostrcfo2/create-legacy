package nl.melonstudios.create.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.tileentity.*;

public class CommonProxy {
    public Side getSide() {
        return Side.SERVER;
    }

    protected static ResourceLocation create(String path) {
        return new ResourceLocation("create", path);
    }
    public void setItemModel(Item item, int meta, String file) {}
    public void setItemModel(Item item, String file) {
        this.setItemModel(item, 0, file);
    }
    public void setItemModel(Item item) {
        this.setItemModel(item, item.getRegistryName().getResourcePath());
    }

    public void setItemModel(Block item, int meta, String file) {
        this.setItemModel(Item.getItemFromBlock(item), meta, file);
    }
    public void setItemModel(Block item, String file) {
        this.setItemModel(Item.getItemFromBlock(item), file);
    }
    public void setItemModel(Block item) {
        this.setItemModel(Item.getItemFromBlock(item));
    }

    public void clientPreInit(FMLPreInitializationEvent event) {}
    public void clientInit(FMLInitializationEvent event) {}
    public void clientPostInit(FMLPostInitializationEvent event) {}

    public void registerTileEntities() {
        this.registerTE(TileEntityShaft.class, "shaft");
        this.registerTE(TileEntityCogwheel.class, "cogwheel");
        this.registerTE(TileEntityGearshift.class, "gearshift");
        this.registerTE(TileEntityClutch.class, "clutch");
        this.registerTE(TileEntityHandCrank.class, "hand_crank");
    }

    public void pork() {}

    private void registerTE(Class<? extends TileEntity> te, String name) {
        GameRegistry.registerTileEntity(te, create(name));
    }
}
