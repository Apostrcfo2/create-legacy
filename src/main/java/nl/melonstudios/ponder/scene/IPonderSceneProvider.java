package nl.melonstudios.ponder.scene;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;
import java.util.function.Supplier;

@SideOnly(Side.CLIENT)
public interface IPonderSceneProvider extends Supplier<PonderScene> {
    @Override
    PonderScene get();

    static IPonderSceneProvider of(NBTTagCompound nbt) {
        return new FromNBT(nbt);
    }
    static IPonderSceneProvider of(Map<BlockPos, IBlockState> map) {
        return new FromMap(map);
    }
    static IPonderSceneProvider of(PonderScene scene) {
        return new FromPonderScene(scene);
    }

    class FromNBT implements IPonderSceneProvider {
        private final NBTTagCompound nbt;

        private FromNBT(NBTTagCompound nbt) {
            this.nbt = nbt;
        }

        @Override
        public PonderScene get() {
            return new PonderScene(this.nbt);
        }
    }

    class FromMap implements IPonderSceneProvider {
        private final Map<BlockPos, IBlockState> map;

        private FromMap(Map<BlockPos, IBlockState> map) {
            this.map = map;
        }

        @Override
        public PonderScene get() {
            return new PonderScene(this.map, true);
        }
    }

    class FromPonderScene implements IPonderSceneProvider {
        private final PonderScene scene;

        private FromPonderScene(PonderScene scene) {
            this.scene = scene;
        }

        @Override
        public PonderScene get() {
            return new PonderScene(this.scene);
        }
    }
}
