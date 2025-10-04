package nl.melonstudios.create.tileentity.marker;

import nl.melonstudios.create.util.SubInteractionBox;

import java.util.Collection;

public interface ITileEntityWithSubInteractions {
    Collection<SubInteractionBox> getSubInteractionBoxes();
}
