package org.amse.bomberman.server.gameservice.models;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ModelListener {

    void statsChanged();

    void gameMapChanged();

    void end();
}
