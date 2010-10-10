/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameservice;

import org.amse.bomberman.util.Pair;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface Field {

    /**
     * @return field dimension.
     */
    int getDimension();

    /**
     * @return max players for this field.
     */
    int getMaxPlayers();

    /**
     * @param position coordinate on field to get value from.
     * @return value at specified position.
     */
    int getValue(Pair position);

    /**
     * Sets value at specified position.
     *
     * @param position coordinate on field where to set value.
     * @param value value to set.
     */
    void setValue(Pair position, int value);

}
