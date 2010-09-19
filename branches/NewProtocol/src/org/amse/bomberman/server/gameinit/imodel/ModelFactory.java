/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.imodel;

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.GameMap;
import org.amse.bomberman.server.gameinit.imodel.impl.Model;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ModelFactory {

    private ModelFactory() {
    }

    public static IModel createModel(Game game, GameMap gameMap) {
        return new Model(gameMap, game);
    }
}
