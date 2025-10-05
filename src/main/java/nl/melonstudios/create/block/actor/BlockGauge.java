package nl.melonstudios.create.block.actor;

import com.melonstudios.melonlib.misc.AABB;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.block.BlockKineticHorizontalAxisBase;
import nl.melonstudios.create.tileentity.TileEntityGaugeBase;
import nl.melonstudios.create.tileentity.actor.TileEntitySpeedometer;
import nl.melonstudios.create.tileentity.actor.TileEntityStressometer;
import nl.melonstudios.create.util.Color;
import nl.melonstudios.create.util.TextBuilder;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("deprecation")
public class BlockGauge extends BlockKineticHorizontalAxisBase implements ITileEntityProvider {
    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return this.type == Type.SPEED ? new TileEntitySpeedometer() : new TileEntityStressometer();
    }

    public static final AxisAlignedBB GAUGE_AABB = AABB.create(1, 0, 1, 15, 14, 15);

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return GAUGE_AABB;
    }

    public enum Type {
        SPEED, STRESS
    }

    public final Type type;

    public BlockGauge(MapColor color, SoundType soundType, Type gaugeType) {
        super(color, soundType);
        this.type = gaugeType;
    }

    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        TileEntityGaugeBase gauge = getGaugeTE(worldIn, pos);
        if (gauge == null) return;
        if (gauge.dialTarget == 0) return;
        int color = gauge.color;

        for (EnumFacing facing : EnumFacing.VALUES) {
            if (this.renderHeadOnFace(worldIn, pos, stateIn, facing)) {
                Vector3f rgb = new Color(color).asVectorF();
                Vec3d faceVec = new Vec3d(facing.getDirectionVec());
                EnumFacing positiveFacing = EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.POSITIVE, facing.getAxis());
                Vec3d positiveFacingVec = new Vec3d(positiveFacing.getDirectionVec());
                int particleCount = gauge.dialTarget > 1 ? 4 : 1;

                if (particleCount == 1 && rand.nextFloat() > 0.25F) continue;

                for (int i = 0; i < particleCount; i++) {
                    Vec3d scalar = new Vec3d(1, 1, 1).subtract(positiveFacingVec);
                    Vec3d randomized = new Vec3d(
                            rand.nextFloat() * 0.5F - 0.25F,
                            rand.nextFloat() * 0.5F - 0.25F,
                            rand.nextFloat() * 0.5F - 0.25F
                    );
                    Vec3d mul = new Vec3d(
                            randomized.x * scalar.x,
                            randomized.y * scalar.y,
                            randomized.z * scalar.z
                            ).normalize()
                            .scale(.3F);
                    Vec3d offset = new Vec3d(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5)
                            .add(faceVec.scale(.55)).add(mul);
                    CreateLegacy.proxy.spawnRedstoneFX(worldIn, offset.x, offset.y, offset.z, mul.x, mul.y, mul.z, 1, rgb.x, rgb.y, rgb.z);
                }
            }
        }
    }

    @Nullable
    public static TileEntityGaugeBase getGaugeTE(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        return te instanceof TileEntityGaugeBase ? (TileEntityGaugeBase) te : null;
    }

    public boolean renderHeadOnFace(World world, BlockPos pos, IBlockState state, EnumFacing face) {
        if (face.getAxis().isVertical()) return false;
        if (face.getAxis() == state.getValue(HORIZONTAL_AXIS)) return false;
        return this.shouldSideBeRendered(state, world, pos, face);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
        TileEntityGaugeBase gauge = getGaugeTE(worldIn, pos);
        if (gauge != null) {
            return MathHelper.ceil(MathHelper.clamp(gauge.dialTarget * 14, 0, 15));
        }
        return 0;
    }

    @Override
    public List<String> getGoggleInfo(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        TextBuilder builder = new TextBuilder();
        builder.text("Kinetic Network Stats").enter();
        if (tileEntity instanceof TileEntitySpeedometer) {
            TileEntitySpeedometer te = (TileEntitySpeedometer) tileEntity;

            float speed = Math.abs(te.getSpeed());
            boolean overstressed = te.overstressed;

            builder.formatting(TextFormatting.GRAY).text("Network Speed: ");
            if (overstressed) {
                builder.formatting(TextFormatting.DARK_GRAY).formatting(TextFormatting.STRIKETHROUGH)
                        .text("0 RPM").resetFormat().space().formatting(TextFormatting.DARK_RED).text("Overstressed").enter();
            } else {
                builder.formatting(TextFormatting.AQUA).number(speed).text(" RPM").enter();
            }

            return builder.build();
        }
        if (tileEntity instanceof TileEntityStressometer) {
            TileEntityStressometer te = (TileEntityStressometer) tileEntity;

            float stress = te.getNetworkStress();
            float capacity = te.getNetworkCapacity();
            boolean overstressed = te.overstressed;

            builder.formatting(TextFormatting.GRAY).text("Network Stress: ");
            if (overstressed) {
                builder.formatting(TextFormatting.DARK_GRAY).formatting(TextFormatting.STRIKETHROUGH);
            } else {
                builder.formatting(TextFormatting.AQUA);
            }
            builder.number(stress).text("/").number(capacity).text("su (").number(capacity == 0 ? 0 : (int)((stress / capacity) * 100)).text("%)");
            if (overstressed) {
                builder.resetFormat();
                builder.space();
                builder.formatting(TextFormatting.DARK_RED);
                builder.text("Overstressed");
            }

            return builder.build();
        }
        return Collections.emptyList();
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, IBlockState state, EnumFacing side, float hitX, float hitY, float hitZ) {
        world.setBlockState(pos, state.cycleProperty(HORIZONTAL_AXIS), 3);
        return true;
    }
}
