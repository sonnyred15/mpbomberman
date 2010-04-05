/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server;

import org.amse.bomberman.server.net.IServer;

/**
 *
 * @author Kirilchuk V.E
 */
public interface ServerChangeListener {

    void addedToLog(String line);

    void changed(IServer server);
}
