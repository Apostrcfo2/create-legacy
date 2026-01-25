package nl.melonstudios.create.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleBreaking;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.actor.BlockGauge;
import nl.melonstudios.create.entity.EntityContraptionBearing;
import nl.melonstudios.create.entity.EntityGlue;
import nl.melonstudios.create.entity.RenderContraptionBearing;
import nl.melonstudios.create.entity.RenderGlue;
import nl.melonstudios.create.init.SoundInit;
import nl.melonstudios.create.tesr.*;
import nl.melonstudios.create.tesr.actor.*;
import nl.melonstudios.create.tesr.generator.TESRBearingWindmill;
import nl.melonstudios.create.tesr.generator.TESRHandCrank;
import nl.melonstudios.create.tesr.generator.TESRWaterWheel;
import nl.melonstudios.create.tileentity.*;
import nl.melonstudios.create.tileentity.actor.*;
import nl.melonstudios.create.tileentity.generator.TileEntityBearingWindmill;
import nl.melonstudios.create.tileentity.generator.TileEntityHandCrank;
import nl.melonstudios.create.tileentity.generator.TileEntityWaterWheel;
import nl.melonstudios.create.tileentity.generator.TileEntityWaterWheelTemp;
import nl.melonstudios.ponder.PonderRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {
    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void setItemModel(Item item, int meta, String file) {
        ModelLoader.setCustomModelResourceLocation(item, meta,
                new ModelResourceLocation(new ResourceLocation("create", file), "inventory"));
    }

    @Override
    public void registerTileEntities() {
        this.registerTESR(TileEntityShaft.class, "shaft", new TESRShaft());
        this.registerTESR(TileEntityCogwheel.class, "cogwheel", new TESRCogwheel());
        this.registerTESR(TileEntityGearbox.class, "gearbox", new TESRGearbox<>());
        this.registerTESR(TileEntityGearshift.class, "gearshift", new TESRSplitShaft<>());
        this.registerTESR(TileEntityClutch.class, "clutch", new TESRSplitShaft<>());
        this.registerTESR(TileEntityHandCrank.class, "hand_crank", new TESRHandCrank());
        this.registerTESR(TileEntityWaterWheel.class, "water_wheel", new TESRWaterWheel());
        this.registerTESR(TileEntityWaterWheelTemp.class, "water_wheel_temp", null);
        this.registerTESR(TileEntityTurntable.class, "turntable", new TESRTurntable());
        this.registerTESR(TileEntityBearing.class, "bearing", new TESRBearing<>());
        this.registerTESR(TileEntityBearingWindmill.class, "bearing_windmill", new TESRBearingWindmill<>());
        this.registerTESR(TileEntityDistanceController.class, "distance_controller", new TESRDistanceController());
        this.registerTESR(TileEntitySpeedometer.class, "speedometer", new TESRGauge<>(BlockGauge.Type.SPEED));
        this.registerTESR(TileEntityStressometer.class, "stressometer", new TESRGauge<>(BlockGauge.Type.STRESS));
        this.registerTESR(TileEntityPress.class, "press", new TESRPress<>());
        this.registerTESR(TileEntityDrill.class, "drill", new TESRDrill<>());
        this.registerTESR(TileEntitySaw.class, "saw", new TESRSaw());
        this.registerTESR(TileEntitySawProcessing.class, "saw_processing", new TESRSawProcessing());
        this.registerTESR(TileEntityDeployer.class, "deployer", new TESRDeployer());
        this.registerTESR(TileEntityPlough.class, "plough", null);
        this.registerTESR(TileEntityHarvester.class, "harvester", new TESRHarvester());
        this.registerTESR(TileEntityStorageInterface.class, "storage_interface", new TESRContraptionInterface<>());
        this.registerTESR(TileEntityMillstone.class, "millstone", null);
        this.registerTESR(TileEntityDepot.class, "depot", new TESRDepot());
        this.registerTESR(TileEntityBasin.class, "basin", new TESRBasin());
        this.registerTESR(TileEntityChute.class, "chute", new TESRChute());
    }

    @Override
    public void registerEntityRenderers() {
        RenderingRegistry.registerEntityRenderingHandler(EntityGlue.class, RenderGlue::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityContraptionBearing.class, RenderContraptionBearing::new);
    }

    @Override
    public void pork() {
        TESRKineticBase.pork();
    }

    private void registerTESR(Class<? extends TileEntity> te, String name, @Nullable TileEntitySpecialRenderer<?> renderer) {
        GameRegistry.registerTileEntity(te, create(name));
        if (renderer != null) TileEntityRendererDispatcher.instance.renderers.put(te, renderer);
    }

    @Override
    public void spawnRedstoneFX(double x, double y, double z, double mx, double my, double mz,
                                float size, float r, float g, float b) {
        ParticleRedstone particle = (ParticleRedstone) Objects.requireNonNull(Minecraft.getMinecraft().effectRenderer
                .spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(), x, y, z, mx, my, mz));
        particle.setRBGColorF(r, g, b);
        particle.multipleParticleScaleBy(size);
    }

    @Override
    public void spawnItemFX(double x, double y, double z, double mx, double my, double mz, int id, int meta) {
        Minecraft.getMinecraft().effectRenderer.spawnEffectParticle(
                EnumParticleTypes.ITEM_CRACK.getParticleID(),
                x, y, z, mx, my, mz, id, meta
        );
    }

    @Override
    public void millstoneFX(TileEntityMillstone millstone) {
        BlockPos pos = millstone.getPos();
        Random rnd = millstone.getWorld().rand;
        ItemStack in = millstone.input;
        if (in.isEmpty()) return;
        int id = Item.getIdFromItem(in.getItem());
        int meta = in.getMetadata();
        for (int i = 0; i < 5; i++) {
            millstone.getWorld().playSound(Minecraft.getMinecraft().player, pos,
                    SoundInit.block_millstone_ambient, SoundCategory.BLOCKS,
                    0.35F, 0.8F + millstone.getWorld().rand.nextFloat() * 0.3F
            );
            double xOffset = rnd.nextDouble() - 0.5;
            double zOffset = rnd.nextDouble() - 0.5;
            Minecraft.getMinecraft().effectRenderer
                    .spawnEffectParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(),
                            pos.getX() + xOffset + 0.5, pos.getY() + 0.5, pos.getZ() + zOffset + 0.5,
                            xOffset * 0.2, 0.1, zOffset * 0.2, id, meta);
            Minecraft.getMinecraft().effectRenderer
                    .spawnEffectParticle(EnumParticleTypes.CRIT.getParticleID(),
                            pos.getX() + rnd.nextDouble(), pos.getY() + 0.5, pos.getZ() + rnd.nextDouble(),
                            0, 0, 0);
        }
    }
    @Override
    public void mixerFX(TileEntityBasin basin, double x, double y, double z) {
        List<FluidStack> fluids = new ArrayList<>();
        FluidStack fluid1 = basin.tank1.getFluid();
        FluidStack fluid2 = basin.tank2.getFluid();
        FluidStack fluid3 = basin.tank3.getFluid();
        if (fluid1 != null) fluids.add(fluid1);
        if (fluid2 != null) fluids.add(fluid2);
        if (fluid3 != null) fluids.add(fluid3);
        TextureMap map = Minecraft.getMinecraft().getTextureMapBlocks();
        double offset = CreateLegacy.rand.nextDouble();
        if (!fluids.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                FluidStack fluid = fluids.get(CreateLegacy.rand.nextInt(fluids.size()));
                TextureAtlasSprite sprite = map.getAtlasSprite(fluid.getFluid().getStill(fluid).toString());
                double angle = (1.2566370614359172) * (i+offset);
                double vx = Math.sin(angle);
                double vz = Math.cos(angle);
                this.getTexturePieceParticle(x, y, z, vx, 0.5, vz, sprite);
            }
        }
        if (!basin.inventory.isEmpty()) {
            for (int i = 0; i < 5; i++) {
                ItemStack item = basin.inventory.get(CreateLegacy.rand.nextInt(basin.inventory.size()));
                double angle = (1.2566370614359172) * (i+offset+0.1);
                double vx = Math.sin(angle);
                double vz = Math.cos(angle);
                this.spawnItemFX(x, y, z, vx, 0.5, vz, item);
            }
        }
    }

    public void getTexturePieceParticle(double x, double y, double z, double vx, double vy, double vz, TextureAtlasSprite sprite) {
        ParticleBreaking particle = (ParticleBreaking) Minecraft.getMinecraft().effectRenderer
                .spawnEffectParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), x, y, z, vx, vy, vz, 0, 0);
        Objects.requireNonNull(particle, "null particle!");
        particle.setParticleTexture(sprite);
    }

    @Override
    public void initiatePonders() {
        PonderRegistry.bootstrap();
    }
}
