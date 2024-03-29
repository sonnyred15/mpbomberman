package org.amse.bomberman.server.gameservice.models;

import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.gameservice.gamemap.impl.GameMap;
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
