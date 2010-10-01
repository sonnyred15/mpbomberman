/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers;

import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ResponseCreator;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.gameservice.listeners.GameChangeListener;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.InGameState;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates.NotJoinedState;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NetGamePlayer implements GamePlayer, GameChangeListener {

    private String nickName = "unnamed";
    private int playerId = -1;
    private final ResponseCreator protocol = new ResponseCreator();
    private final Controller controller;

    public NetGamePlayer(Controller controller) {
        this.controller = controller;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public int getPlayerId() {
        return playerId;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return this.nickName;
    }

    public void resetId() {
        this.playerId = -1;
    }

    public void parametersChanged(Game game) {
        this.controller.sendToClient(protocol.ok(ProtocolConstants.NOTIFICATION_MESSAGE_ID,
                    ProtocolConstants.UPDATE_GAME_INFO));
    }

    public void gameStarted(Game game) {
        this.controller.setState(new InGameState(controller, game));
        if (!game.isGameOwner(this)) {
            this.controller.sendToClient(protocol.ok(ProtocolConstants.NOTIFICATION_MESSAGE_ID,
                    ProtocolConstants.MESSAGE_GAME_START));
        }
    }

    public void gameTerminated(Game game) {
        this.controller.setState(new NotJoinedState(controller));
        this.resetId();
        if (!game.isGameOwner(this)) {
            this.controller.sendToClient(protocol.ok(ProtocolConstants.NOTIFICATION_MESSAGE_ID,
                    ProtocolConstants.MESSAGE_GAME_KICK));
        }
    }

    public void newChatMessage(String message) {
        this.controller.sendToClient(protocol.chatMessage(message));
    }

    public void fieldChanged() {
        this.controller.sendToClient(protocol.ok(ProtocolConstants.NOTIFICATION_MESSAGE_ID,
                    ProtocolConstants.UPDATE_GAME_MAP));
    }

    public void gameEnded(Game game) {
        this.controller.sendToClient(protocol.gameEnd(game));
    }

    public void statsChanged(Game game) {
        this.controller.sendToClient(protocol.playersStats(game));
    }
}
