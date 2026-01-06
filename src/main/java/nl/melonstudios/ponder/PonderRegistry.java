package nl.melonstudios.ponder;

import com.melonstudios.melonlib.misc.MetaItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import nl.melonstudios.create.CreateLegacy;
import nl.melonstudios.create.init.BlockInit;
import nl.melonstudios.create.init.ItemInit;
import nl.melonstudios.create.tileentity.TileEntityDepot;
import nl.melonstudios.ponder.plan.PonderPlan;
import nl.melonstudios.ponder.plan.PonderPlanBuilder;
import nl.melonstudios.ponder.plan.action.ActionSetScene;
import nl.melonstudios.ponder.scene.IPonderSceneProvider;
import nl.melonstudios.ponder.world.EnumEntityPonder;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class PonderRegistry {
    private static final Map<MetaItem, PonderContainer> PONDERS = new HashMap<>();


    public static void registerPonder(MetaItem item, PonderContainer container) {
        PONDERS.put(item, container);
    }

    public static PonderContainer getPonder(ItemStack item) {
        return PONDERS.get(MetaItem.of(item));
    }
    public static boolean hasPonder(ItemStack item) {
        return PONDERS.containsKey(MetaItem.of(item));
    }

    public static void bootstrap() {
        try {
            NBTTagCompound test = getClassNBT("assets/create/ponders/test.nbt");
            registerPonder(MetaItem.of(ItemInit.WRENCH, 0),
                    new PonderContainer(PonderPlan.withBuilder((builder) -> {
                        builder.setInitialScene("main");
                        builder.setInitialSubject("Testing!");
                        builder.setInitialScale(16.0F);
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
                            creeper.posZ = 0.5;
                            creeper.posY = 1;
                            return creeper;
                        });
                    })).addSceneProvider("main", IPonderSceneProvider.of(test))
            );
        } catch (IOException e) {
            throw new RuntimeException("Failed to register Ponders", e);
        }
    }

    private static NBTTagCompound getClassNBT(String path) throws IOException {
        try (
                InputStream stream = Objects.requireNonNull(CreateLegacy.class.getClassLoader().getResourceAsStream(path), "Null file");
                DataInputStream data = new DataInputStream(new BufferedInputStream(stream))
        ) {
            return CompressedStreamTools.read(data);
        }
    }
}
