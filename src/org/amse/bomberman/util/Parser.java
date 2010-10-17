package org.amse.bomberman.util;

import java.util.List;
import org.amse.bomberman.client.models.gamemodel.GameMap;
import org.amse.bomberman.client.models.gamemodel.Player;

/**
 *
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public interface Parser {

    GameMap parseGameMap(List<String> data);

    Player parsePlayer(List<String> data);
}
