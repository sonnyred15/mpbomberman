package org.amse.bomberman.server.gameservice.gamemap.impl;

import org.amse.bomberman.util.Pair;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameMapObject {

    /**
     * Returns ID of this moveableObject.
     * @return ID of this moveableObject.
     */
    int getId();

    /**
     * Returns current position of moveableObject.
     * @return current position of moveableObject.
     */
    Pair getPosition();
}
