package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.protocol.responses.ResponseCreator;
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

    public ProtocolMessage<Integer, String> createGame(String gameMapName,
            String gameName, int maxPlayers) {
        return protocol.illegalState("create game", stateName);
    }

    public ProtocolMessage<Integer, String> joinGame(int gameID) {
        return protocol.illegalState("join game", stateName);
    }

    public ProtocolMessage<Integer, String> doMove(Direction dir) {
        return protocol.illegalState("do move", stateName);
    }

    public ProtocolMessage<Integer, String> getGameMapInfo() {
        return protocol.illegalState("get game map info", stateName);
    }

    public ProtocolMessage<Integer, String> startGame() {
        return protocol.illegalState("start game", stateName);
    }

    public ProtocolMessage<Integer, String> leave() {
        return protocol.illegalState("leave", stateName);
    }

    public ProtocolMessage<Integer, String> placeBomb() {
        return protocol.illegalState("place bomb", stateName);
    }

    public ProtocolMessage<Integer, String> getGameStatus() {
        return protocol.illegalState("get game status", stateName);
    }

    public ProtocolMessage<Integer, String> addBot(String name) {
        return protocol.illegalState("add bot", stateName);
    }

    public ProtocolMessage<Integer, String> getGameInfo() {
        return protocol.illegalState("get game info", stateName);
    }

    public ProtocolMessage<Integer, String> addMessageToChat(String message) {
        return protocol.illegalState("add chat message", stateName);
    }

    public ProtocolMessage<Integer, String> getNewMessagesFromChat() {
        return protocol.illegalState("get new messages from chat", stateName);
    }

    public ProtocolMessage<Integer, String> kickPlayer(int playerId)  {
        return protocol.illegalState("remove bot", stateName);
    }

    public ProtocolMessage<Integer, String> getGamePlayersStats() {
        return protocol.illegalState("get players stats", stateName);
    }
}
