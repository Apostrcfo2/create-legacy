package nl.melonstudios.ponder.plan.action;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.IVirtualizable;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.scene.PonderScene;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.Map;

@SideOnly(Side.CLIENT)
public class ActionSetScene implements IPonderAction {
    private final String key;

    public ActionSetScene(String name) {
        this.key = name;
    }

    @Override
    public void accept(WorldPonder ponder) {
        if (ponder.scenes.containsKey(this.key)) {
            ponder.scene = ponder.scenes.get(this.key);
        } else {
            PonderScene newScene = ponder.container.sceneProviders.get(this.key).get();
            ponder.scenes.put(this.key, newScene);
            for (Map.Entry<BlockPos, IBlockState> entry : newScene.blocks.entrySet()) {
                BlockPos pos = entry.getKey();
                IBlockState state = entry.getValue();
                if (state.getBlock().hasTileEntity(state)) {
                    ITileEntityProvider provider = (ITileEntityProvider) state.getBlock();
                    TileEntity te = provider.createNewTileEntity(ponder, state.getBlock().getMetaFromState(state));
                    if (te != null) {
                        if (te instanceof IVirtualizable) {
                            ((IVirtualizable) te).markAsVirtual();
                        }
                        te.setWorld(ponder);
                        te.setPos(pos);
                        newScene.tileEntities.put(pos, te);
                    }
                }
            }
            ponder.scene = newScene;
        }
    }

    @Override
    public boolean requiresMeshUpdate() {
        return true;
    }
}
