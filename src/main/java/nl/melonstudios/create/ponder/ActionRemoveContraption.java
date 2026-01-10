package nl.melonstudios.create.ponder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.create.extensions.IExtensionPonderScene;
import nl.melonstudios.create.kinetics.contraption.ContraptionRendering;
import nl.melonstudios.create.tileentity.TileEntityKinetic;
import nl.melonstudios.create.util.BlockRotationHelper;
import nl.melonstudios.ponder.plan.IPonderAction;
import nl.melonstudios.ponder.world.WorldPonder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class ActionRemoveContraption implements IPonderAction {
    private final String name;
    private final boolean placeBlocks;

    public ActionRemoveContraption(String name, boolean placeBlocks) {
        this.name = name;
        this.placeBlocks = placeBlocks;
    }

    @Override
    public void accept(WorldPonder ponder) {
        IExtensionPonderScene extension = (IExtensionPonderScene) ponder.scene;
        List<PonderContraption> bin = new ArrayList<>();
        for (PonderContraption contraption : extension.create$getPonderContraptions()) {
            if (this.name.equals(contraption.name)) {
                bin.add(contraption);
            }
        }
        extension.create$getPonderContraptions().removeAll(bin);
        if (this.placeBlocks) {
            for (PonderContraption contraption : bin) {
                BlockPos self = new BlockPos(contraption.x, contraption.y, contraption.z);
                if (contraption.type.isBearing) {
                    Rotation rotation = BlockRotationHelper.getRotationForAngle(contraption.param1);
                    EnumFacing.Axis axis = contraption.type.bearingAxis;
                    for (Map.Entry<BlockPos, IBlockState> entry : contraption.contraption.blocks.entrySet()) {
                        BlockPos pos = BlockRotationHelper.transform(self, axis, rotation, entry.getKey());
                        ponder.setBlockState(pos, BlockRotationHelper.rotate(entry.getValue(), axis, rotation));
                        TileEntity te = contraption.contraption.tileEntities.get(entry.getKey());
                        if (te != null) {
                            te.setPos(pos);
                            te.validate();
                            ponder.setTileEntity(pos, te);
                            if (te instanceof TileEntityKinetic) {
                                TileEntityKinetic kinetic = (TileEntityKinetic)te;
                                kinetic.attachKinetics();
                            }
                        }
                    }
                } else {
                    for (Map.Entry<BlockPos, IBlockState> entry : contraption.contraption.blocks.entrySet()) {
                        BlockPos pos = self.add(entry.getKey());
                        ponder.setBlockState(pos, entry.getValue());
                        TileEntity te = contraption.contraption.tileEntities.get(entry.getKey());
                        if (te != null) {
                            te.setPos(pos);
                            te.validate();
                            ponder.setTileEntity(pos, te);
                            if (te instanceof TileEntityKinetic) {
                                TileEntityKinetic kinetic = (TileEntityKinetic)te;
                                kinetic.attachKinetics();
                            }
                        }
                    }
                }
            }
        }
        for (PonderContraption contraption : bin) {
            ContraptionRendering.contraptionFinalized(contraption.contraption);
        }
    }

    @Override
    public boolean requiresMeshUpdate() {
        return this.placeBlocks;
    }
}
