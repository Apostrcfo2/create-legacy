package nl.melonstudios.create.init;

import com.melonstudios.melonlib.misc.MetaItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import nl.melonstudios.create.block.actor.BlockBearingBase;
import nl.melonstudios.create.kinetics.contraption.GluedSurface;
import nl.melonstudios.create.ponder.CreatePonderPlanBuilder;
import nl.melonstudios.create.ponder.PonderContraption;
import nl.melonstudios.create.tileentity.TileEntityCogwheel;
import nl.melonstudios.create.tileentity.TileEntityDepot;
import nl.melonstudios.ponder.PonderContainer;
import nl.melonstudios.ponder.PonderRegistrar;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.scene.IPonderSceneProvider;
import nl.melonstudios.ponder.world.EnumEntityPonder;

import java.io.IOException;

public class PonderInit {
    public static void register(PonderRegistrar registrar) {
        try {
            IPonderSceneProvider test = IPonderSceneProvider.of(registrar.getClassNBT("assets/create/ponders/test.nbt"));
            registrar.register(MetaItem.of(ItemInit.WRENCH, 0),
                    new PonderContainer(PonderPlan.withBuilder("create.test", (builder) -> {
                        builder.setInitialScene("main");
                        builder.setInitialSubject("Testing!");
                        builder.setInitialOffset(-2, -2, -2);

                        builder.pause(20);
                        builder.setSubject("yummers");
                        BlockPos depot = new BlockPos(0, 1, 0);
                        builder.setBlock(depot, BlockInit.DEPOT.getDefaultState());
                        builder.setTileEntity(depot, TileEntityDepot::new, false);
                        builder.modifyTileEntity(depot, TileEntityDepot.class, (te) -> {
                            te.mainItem = new ItemStack(ItemInit.SUPER_GLUE);
                        });

                        builder.pause(20);
                        builder.setSubject("creeper aw man");
                        builder.pause(1);
                        builder.addEntity(EnumEntityPonder.NON_TICKING_RENDER_ONLY, (world) -> {
                            EntityCreeper creeper = new EntityCreeper(world);
                            creeper.posX = 1.5;
                            creeper.posY = 1;
                            creeper.posZ = 0.5;
                            return creeper;
                        });
                        builder.addTooltip(20, 1.5F, 2.0F, 0.5F, "the creeper is danger");
                    }))
                    .addSceneProvider("main", test)
            );
            IPonderSceneProvider mechanicalBearing
                    = IPonderSceneProvider.of(registrar.getClassNBT("create", "mechanical_bearing"));
            IPonderSceneProvider mechanicalBearingWithContraption
                    = IPonderSceneProvider.of(registrar.getClassNBT("create", "mechanical_bearing_with_contraption"));
            registrar.register(MetaItem.of(ItemInit.SUPER_GLUE, 0),
                    new PonderContainer(CreatePonderPlanBuilder.builder("create.mechanical_bearing", (builder) -> {
                        builder.setInitialScene("mechanical_bearing");
                        builder.setInitialSubject("create.mechanical_bearing");
                        builder.setInitialOffset(-5, -2, -5);

                        builder.setSpeedEnMasse(16.0F, te -> {
                            if (te instanceof TileEntityCogwheel) {
                                return !((TileEntityCogwheel)te).isLarge();
                            }
                            return true;
                        });
                        builder.setSpeedEnMasse(-8.0F, te -> {
                            if (te instanceof TileEntityCogwheel) {
                                return ((TileEntityCogwheel)te).isLarge();
                            }
                            return false;
                        });
                        builder.pause(40);
                        builder.addTooltip(100, 4.5F, 2.5F, 4.5F, "The bearing can be used to rotate Contraptions");
                        builder.pause(150);
                        builder.setScene("mechanical_bearing_with_contraption");
                        builder.setSpeedEnMasse(16.0F, te -> {
                            if (te instanceof TileEntityCogwheel) {
                                return !((TileEntityCogwheel)te).isLarge();
                            }
                            return true;
                        });
                        builder.setSpeedEnMasse(-8.0F, te -> {
                            if (te instanceof TileEntityCogwheel) {
                                return ((TileEntityCogwheel)te).isLarge();
                            }
                            return false;
                        });
                        builder.addTooltip(100, 4.5F, 4.0F, 4.5F, "Build something on the plateau...");
                        builder.pause(150);
                        BlockPos bearingPos = new BlockPos(4, 2, 4);
                        for (EnumFacing side : EnumFacing.HORIZONTALS) {
                            builder.addGlue(new GluedSurface(new BlockPos(4, 3, 4), side));
                            builder.addGlue(new GluedSurface(new BlockPos(4, 4, 4), side));
                        }
                        builder.addTooltip(100, 4.5F, 4.0F, 4.5F, "...and properly glue it");
                        builder.pause(150);
                        builder.setBlock(bearingPos, BlockInit.BEARING.getDefaultState()
                                .withProperty(BlockBearingBase.FACING, EnumFacing.UP)
                                .withProperty(BlockBearingBase.ASSEMBLED, true));
                        builder.setSpeed(bearingPos, 16.0F);
                        builder.assembleContraption("bearing-contraption", new BlockPos(4, 3, 4), bearingPos,
                                PonderContraption.Type.ROTATE_Y, (ctrp) -> ctrp.param1 -= 4.8F);
                        builder.addTooltip(300, 4.5F, 4.0F, 4.5F, "Then assemble it, and the Contraption will move");
                    }))
                    .addSceneProvider("mechanical_bearing", mechanicalBearing)
                    .addSceneProvider("mechanical_bearing_with_contraption", mechanicalBearingWithContraption)
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to register Ponders", e);
        }
    }
}
