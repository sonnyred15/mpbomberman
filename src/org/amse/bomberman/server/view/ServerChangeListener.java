
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.view;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.net.IServer;

/**
 * Interface of server change listener.
 * @author Kirilchuk V.E
 */
public interface ServerChangeListener {

    /**
     * Method that server calls to notify it`s change listeners
     * about log changing.
     * @param line message that was added to log.
     */
    void addedToLog(String line);

    /**
     * Method that server calls to notify it`s change listeners
     * about changing of it`s state.
     * @param server reference on server object that changed.
     */
    void changed(IServer server);
}
