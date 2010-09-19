/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit.renameme;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.Controller;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NotJoinedState extends AbstractClientState {

    private static final String STATE_NAME = "Not Joined";
    private final Controller controller;

    public NotJoinedState(Controller controller) {
        super(STATE_NAME);
        this.controller = controller;
    }

    @Override
    public ProtocolMessage<Integer, String> createGame(String gameMapName, String gameName, int maxPlayers) {
        try {
            Game game = Creator.createGame(controller.getSession().getGameStorage(),
                    controller, gameMapName, gameName, maxPlayers);
            game.addGameEndedListener(controller);
            controller.setState(new InLobbyState(controller, game));
            return protocol.ok2(ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                    "Game created.");
        } catch (FileNotFoundException ex) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Map wasn`t founded on server." + " Map=" + gameMapName);
            return protocol.notOK2(
                    ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                    "No such map on server.");
        } catch (IOException ex) {
            System.err.println("Session: createGame error while loadimg map. "
                    + " Map=" + gameMapName + " " + ex.getMessage());
            return protocol.notOK2(
                    ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                    "Error on server side, while loading map.");
        }
    }

    @Override
    public ProtocolMessage<Integer, String> joinGame(int gameID) {
        return super.joinGame(gameID);
    }
}
