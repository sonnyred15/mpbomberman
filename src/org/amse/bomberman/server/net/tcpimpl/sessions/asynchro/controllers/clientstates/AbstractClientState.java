package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.impl.ProtocolMessage;
import org.amse.bomberman.protocol.impl.responses.ResponseCreator;
import org.amse.bomberman.util.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public abstract class AbstractClientState implements ClientState {
    protected ResponseCreator protocol = new ResponseCreator();

    private final String stateName;

    public AbstractClientState(String stateName) {
        this.stateName = stateName;
    }

    @Override
    public ProtocolMessage createGame(String gameMapName,
            String gameName, int maxPlayers) {
        return protocol.illegalState("create game", stateName);
    }

    @Override
    public ProtocolMessage joinGame(int gameID) {
        return protocol.illegalState("join game", stateName);
    }

    @Override
    public ProtocolMessage doMove(Direction dir) {
        return protocol.illegalState("do move", stateName);
    }

    @Override
    public ProtocolMessage getGameMapInfo() {
        return protocol.illegalState("get game map info", stateName);
    }

    @Override
    public ProtocolMessage startGame() {
        return protocol.illegalState("start game", stateName);
    }

    @Override
    public ProtocolMessage leave() {
        return protocol.illegalState("leave", stateName);
    }

    @Override
    public ProtocolMessage placeBomb() {
        return protocol.illegalState("place bomb", stateName);
    }

    @Override
    public ProtocolMessage getGameStatus() {
        return protocol.illegalState("get game status", stateName);
    }

    @Override
    public ProtocolMessage addBot(String name) {
        return protocol.illegalState("add bot", stateName);
    }

    @Override
    public ProtocolMessage getGameInfo() {
        return protocol.illegalState("get game info", stateName);
    }

    @Override
    public ProtocolMessage addMessageToChat(String message) {
        return protocol.illegalState("add chat message", stateName);
    }

    @Override
    public ProtocolMessage getNewMessagesFromChat() {
        return protocol.illegalState("get new messages from chat", stateName);
    }

    @Override
    public ProtocolMessage kickPlayer(int playerId)  {
        return protocol.illegalState("remove bot", stateName);
    }

    @Override
    public ProtocolMessage getGamePlayersStats() {
        return protocol.illegalState("get players stats", stateName);
    }
}
