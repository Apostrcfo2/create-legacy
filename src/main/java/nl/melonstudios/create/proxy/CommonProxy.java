package nl.melonstudios.create.proxy;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.tileentity.*;
import nl.melonstudios.create.tileentity.actor.*;
import nl.melonstudios.create.tileentity.generator.TileEntityBearingWindmill;
import nl.melonstudios.create.tileentity.generator.TileEntityHandCrank;
import nl.melonstudios.create.tileentity.generator.TileEntityWaterWheel;
import nl.melonstudios.create.tileentity.generator.TileEntityWaterWheelTemp;

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
        this.registerTE(TileEntityGearbox.class, "gearbox");
        this.registerTE(TileEntityGearshift.class, "gearshift");
        this.registerTE(TileEntityClutch.class, "clutch");
        this.registerTE(TileEntityHandCrank.class, "hand_crank");
        this.registerTE(TileEntityWaterWheel.class, "water_wheel");
        this.registerTE(TileEntityWaterWheelTemp.class, "water_wheel_temp");
        this.registerTE(TileEntityTurntable.class, "turntable");
        this.registerTE(TileEntityBearing.class, "bearing");
        this.registerTE(TileEntityBearingWindmill.class, "bearing_windmill");
        this.registerTE(TileEntityDistanceController.class, "distance_controller");
        this.registerTE(TileEntitySpeedometer.class, "speedometer");
        this.registerTE(TileEntityStressometer.class, "stressometer");
        this.registerTE(TileEntityPress.class, "press");
        this.registerTE(TileEntityMixer.class, "mixer");
        this.registerTE(TileEntityDrill.class, "drill");
        this.registerTE(TileEntitySaw.class, "saw");
        this.registerTE(TileEntitySawProcessing.class, "saw_processing");
        this.registerTE(TileEntityDeployer.class, "deployer");
        this.registerTE(TileEntityPlough.class, "plough");
        this.registerTE(TileEntityHarvester.class, "harvester");
        this.registerTE(TileEntityStorageInterface.class, "storage_interface");
        this.registerTE(TileEntityMillstone.class, "millstone");
        this.registerTE(TileEntityBlazeBurner.class, "blaze_burner");
        this.registerTE(TileEntityDepot.class, "depot");
        this.registerTE(TileEntityBasin.class, "basin");
        this.registerTE(TileEntityChute.class, "chute");
        this.registerTE(TileEntityItemDrain.class, "item_drain");
    }
    public void registerEntityRenderers() {}

    public void pork() {}

    private void registerTE(Class<? extends TileEntity> te, String name) {
        GameRegistry.registerTileEntity(te, create(name));
    }

    public void spawnRedstoneFX(double x, double y, double z, double mx, double my, double mz, float size, float r, float g, float b) {

    }
    public void spawnItemFX(double x, double y, double z, double mx, double my, double mz, ItemStack stack) {
        this.spawnItemFX(x, y, z, mx, my, mz, Item.getIdFromItem(stack.getItem()), stack.getMetadata());
    }
    public void spawnItemFX(double x, double y, double z, double mx, double my, double mz, int id, int meta) {

    }
    public void millstoneFX(TileEntityMillstone millstone) {

    }
    public void mixerFX(TileEntityBasin basin, double x, double y, double z) {

    }

    public void initiatePonders() {}
}
