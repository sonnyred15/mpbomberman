/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.client.models.gamemodel;

import java.util.List;
import org.amse.bomberman.client.models.gamemodel.impl.ImmutableCell;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface GameMap {

    List<ImmutableCell> getExplosions();

    int getSize();

    int getValue(ImmutableCell cell);

    void setCell(ImmutableCell cell, int value);

    void setExplosions(List<ImmutableCell> expl);

}
