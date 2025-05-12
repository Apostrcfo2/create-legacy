package nl.melonstudios.create.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import nl.melonstudios.create.tesr.*;
import nl.melonstudios.create.tileentity.*;

import javax.annotation.Nullable;

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
    }

    @Override
    public void pork() {
        TESRKineticBase.pork();
    }

    private void registerTESR(Class<? extends TileEntity> te, String name, @Nullable TileEntitySpecialRenderer<?> renderer) {
        GameRegistry.registerTileEntity(te, create(name));
        if (renderer != null) TileEntityRendererDispatcher.instance.renderers.put(te, renderer);
    }
}
