package org.amse.bomberman.util;

import java.util.List;
import org.amse.bomberman.client.models.gamemodel.GameMap;

/**
 *
 * @author Michail Korovkin
 */
public interface IParser {
    public GameMap parse(List<String> list);
}
