
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.util.Pair;

/**
 * Interface that represents moveable object of GameMap.
 * @see GameMap.
 * @author Kirilchuk V.E
 */
public interface MoveableObject {

    /**
     * Setting new position of moveable object.
     * @param newPosition position to set.
     */
    void setPosition(Pair newPosition);

    /**
     * Do something if while moving object was bombed.
     */
    void bombed();

    /**
     * Returns current position of moveableObject.
     * @return current position of moveableObject.
     */
    Pair getPosition();

    /**
     * Returns ID of this moveableObject.
     * @return ID of this moveableObject.
     */
    int getID();
}
