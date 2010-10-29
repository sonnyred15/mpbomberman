package org.amse.bomberman.server;

import org.amse.bomberman.server.gameservice.GameStorage;

/**
 *
 * @author Kirilchuk V.E.
 */
public class ServiceContext {
    private final GameStorage gameStorage = new GameStorage();

    public GameStorage getGameStorage() {
        return gameStorage;
    }
}
