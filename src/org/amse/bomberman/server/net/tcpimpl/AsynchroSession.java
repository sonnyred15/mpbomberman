
/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl;

//~--- non-JDK imports --------------------------------------------------------

import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;
import org.amse.bomberman.util.Creator;
import org.amse.bomberman.util.ILog;
import org.amse.bomberman.util.ProtocolConstants;
import org.amse.bomberman.util.Stringalize;

//~--- JDK imports ------------------------------------------------------------

import java.io.FileNotFoundException;
import java.io.IOException;

import java.net.Socket;

import java.util.ArrayList;
import java.util.List;
import org.amse.bomberman.server.gameinit.GameStorage;
import org.amse.bomberman.server.net.CommandExecutor;
import org.amse.bomberman.server.net.SessionEndListener;

/**
 *
 * @author Kirilchuk V.E.
 */
public class AsynchroSession extends AbstractSession {
    private final Controller         controller;
    private final SingleNotificator  notificator;
    private final SessionEndListener endListener;

    public AsynchroSession(SessionEndListener endListener, Socket clientSocket,
                           GameStorage gameStorage, int sessionID,
                           ILog log) {
       super(clientSocket,gameStorage, sessionID, log);
       this.endListener = endListener;
       this.controller = new Controller(this);
       this.mustEnd = false;
       this.notificator = new SingleNotificator(this);
       this.notificator.start();
    }

    @Override
    protected void freeResources() {
        if (this.controller != null) {
            if (this.controller.getMyGame() != null) { //without this check controller will print error.
                this.controller.tryLeave();
            }
        }

        if (this.endListener != null) {
            this.endListener.sessionTerminated(this);
        }
    }

    @Override
    public CommandExecutor getCommandExecutor() {
        return controller;
    }
}
