package org.amse.bomberman.util;

import java.util.List;
import org.amse.bomberman.client.model.BombMap;

/**
 *
 * @author Michail Korovkin
 */
public interface IParser {
    public BombMap parse(List<String> list);
}
