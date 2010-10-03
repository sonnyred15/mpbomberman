/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameservice.models;

import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GameMap;
import org.amse.bomberman.server.gameservice.models.impl.DefaultModel;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ModelFactory {

    private ModelFactory() {}

    public static Model createModel(Game game, GameMap gameMap) {
        return new DefaultModel(gameMap, game);
    }
}
