package org.amse.bomberman.server.gameservice.impl;

import org.amse.bomberman.protocol.impl.ProtocolConstants;
import org.amse.bomberman.protocol.impl.responses.ResponseCreator;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.InGameState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NetGamePlayer implements GamePlayer, GameChangeListener {

    private String nickName = "unnamed";
    private int playerId = -1;
    private final ResponseCreator protocol = new ResponseCreator();//TODO inject from
    private final Controller controller;

    public NetGamePlayer(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    @Override
    public int getPlayerId() {
        return playerId;
    }

    @Override
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String getNickName() {
        return this.nickName;
    }

    public void resetId() {
        this.playerId = -1;
    }

    @Override
    public void parametersChanged(Game game) {
        this.controller.sendToClient(protocol.ok(ProtocolConstants.GAME_INFO_NOTIFY_ID,
                    ProtocolConstants.UPDATE_GAME_INFO));
    }

    @Override
    public void gameStarted(Game game) {
        this.controller.setState(new InGameState(controller, game));
        if (!game.isGameOwner(this)) {
            this.controller.sendToClient(protocol.ok(ProtocolConstants.GAME_STARTED_NOTIFY_ID,
                    ProtocolConstants.MESSAGE_GAME_START));
        }
    }

    @Override
    public void gameTerminated(Game game) {
        this.controller.setState(new NotJoinedState(controller));
        this.resetId();
        if (!game.isGameOwner(this)) {
            this.controller.sendToClient(protocol.ok(ProtocolConstants.GAME_TERMINATED_NOTIFY_ID,
                    ProtocolConstants.MESSAGE_GAME_KICK));
        }
    }

    @Override
    public void newChatMessage(String message) {
        this.controller.sendToClient(protocol.chatMessage(message));
    }

    @Override
    public void fieldChanged() {
        this.controller.sendToClient(protocol.ok(ProtocolConstants.GAME_FIELD_CHANGED_NOTIFY_ID,
                    ProtocolConstants.UPDATE_GAME_MAP));
    }

    @Override
    public void gameEnded(Game game) {
        this.controller.sendToClient(protocol.gameEnd(game));
    }

    @Override
    public void statsChanged(Game game) {
        this.controller.sendToClient(protocol.playersStats(game));
    }
}
