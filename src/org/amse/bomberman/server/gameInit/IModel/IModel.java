/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameInit.IModel;

import javax.swing.event.ChangeListener;
import org.amse.bomberman.server.gameInit.Player;

/**
 *
 * @author chibis
 */
public interface IModel {

    boolean doMove(Player player, int direction);

    void removePlayer(int playerID);
//  void notifyListeners();
//  void addChangeListener(ChangeListener changeModelListener);
    int[][] getMapArray();

    void printToConsole();

    void changeMapForCurMaxPlayers(int curMaxPlayers);

    int xCoordOf(int playerID);

    int yCoordOf(int playerID);
}
