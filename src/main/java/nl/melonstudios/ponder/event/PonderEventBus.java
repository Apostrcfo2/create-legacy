package nl.melonstudios.ponder.event;

import com.melonstudios.melonlib.misc.Localizer;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import nl.melonstudios.ponder.PonderRegistry;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = Side.CLIENT)
public class PonderEventBus {
    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent event) {
        if (PonderRegistry.hasPonder(event.getItemStack())) {
            event.getToolTip().add(Localizer.translate("ponder.itemTooltip"));
        }
    }
}
