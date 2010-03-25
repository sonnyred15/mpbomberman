/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit;

/**
 *
 * @author Kirilchuk V.E
 */
public interface MoveableObject {

    void setPosition(Pair newPosition);

    void bombed();

    Pair getPosition();

    int getID();
}
