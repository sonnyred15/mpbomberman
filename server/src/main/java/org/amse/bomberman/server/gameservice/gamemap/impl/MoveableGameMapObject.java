package org.amse.bomberman.server.gameservice.gamemap.impl;

import org.amse.bomberman.util.Pair;

/**
 * Interface that represents moveable object of GameMap.
 * @see GameMap.
 * @author Kirilchuk V.E
 */
public interface MoveableGameMapObject extends GameMapObject {

    /**
     * Setting new position of moveable object.
     * @param newPosition position to set.
     */
    @Deprecated
    void setPosition(Pair newPosition);

    /**
     * Do something if while moving object was bombed.
     */
    void bombed();
    
    void move(GameMap where, Pair destination);
}
