package org.amse.bomberman.client.models.gamemodel;

import org.amse.bomberman.client.models.gamemodel.impl.ImmutableCell;
import org.amse.bomberman.util.Direction;

/**
 * Interface for gameMap cell.
 * 
 * @author Kirilchuk V.E.
 */
public interface Cell {

    int getX();

    int getY();

    ImmutableCell nextCell(Direction direction);
}
