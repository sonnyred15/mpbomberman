package org.amse.bomberman.client.util;

import java.util.List;
import org.amse.bomberman.client.models.gamemodel.impl.SimpleGameMap;
import org.amse.bomberman.client.models.gamemodel.Player;

/**
 *
 * @author Michail Korovkin
 * @author Kirilchuk V.E.
 */
public interface Parser {

    SimpleGameMap parseGameMap(List<String> data);

    Player parsePlayer(List<String> data);
}
