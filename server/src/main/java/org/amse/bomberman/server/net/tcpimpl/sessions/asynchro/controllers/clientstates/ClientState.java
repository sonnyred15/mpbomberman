package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.util.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public interface ClientState {

    ProtocolMessage addBot(String name);

    ProtocolMessage addMessageToChat(String message);

    ProtocolMessage createGame(String gameMapName, String gameName, int maxPlayers);

    ProtocolMessage doMove(Direction dir);

    ProtocolMessage getGameInfo();

    ProtocolMessage getGameMapInfo();

    ProtocolMessage getGamePlayersStats();

    ProtocolMessage getGameStatus();

    ProtocolMessage getNewMessagesFromChat();

    ProtocolMessage joinGame(int gameID);

    ProtocolMessage leave();

    ProtocolMessage placeBomb();

    ProtocolMessage kickPlayer(int playerId);

    ProtocolMessage startGame();

}
