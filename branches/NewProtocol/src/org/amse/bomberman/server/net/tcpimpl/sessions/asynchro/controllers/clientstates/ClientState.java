/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ClientState {

    ProtocolMessage<Integer, String> addBot(String name);

    ProtocolMessage<Integer, String> addMessageToChat(String message);

    ProtocolMessage<Integer, String> createGame(String gameMapName, String gameName, int maxPlayers);

    ProtocolMessage<Integer, String> doMove(Direction dir);

    ProtocolMessage<Integer, String> getGameInfo();

    ProtocolMessage<Integer, String> getGameMapInfo();

    ProtocolMessage<Integer, String> getGamePlayersStats();

    ProtocolMessage<Integer, String> getGameStatus();

    ProtocolMessage<Integer, String> getNewMessagesFromChat();

    ProtocolMessage<Integer, String> joinGame(int gameID);

    ProtocolMessage<Integer, String> leave();

    ProtocolMessage<Integer, String> placeBomb();

    ProtocolMessage<Integer, String> removeBot();

    ProtocolMessage<Integer, String> startGame();

}
