package nl.melonstudios.create.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.block.BlockGauge;
import nl.melonstudios.create.tesr.*;
import nl.melonstudios.create.tileentity.*;

import javax.annotation.Nullable;
import java.util.Objects;

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
        this.registerTESR(TileEntitySpeedometer.class, "speedometer", new TESRGauge<>(BlockGauge.Type.SPEED));
        this.registerTESR(TileEntityStressometer.class, "stressometer", new TESRGauge<>(BlockGauge.Type.STRESS));
        this.registerTESR(TileEntityDrill.class, "drill", new TESRDrill<>());
        this.registerTESR(TileEntityMillstone.class, "millstone", null);
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
    public void spawnRedstoneFX(World world, double x, double y, double z, double mx, double my, double mz,
                                float size, float r, float g, float b) {
        ParticleRedstone particle = (ParticleRedstone) Objects.requireNonNull(Minecraft.getMinecraft().effectRenderer
                .spawnEffectParticle(EnumParticleTypes.REDSTONE.getParticleID(), x, y, z, mx, my, mz));
        particle.setRBGColorF(r, g, b);
        particle.multipleParticleScaleBy(size);
    }
}
