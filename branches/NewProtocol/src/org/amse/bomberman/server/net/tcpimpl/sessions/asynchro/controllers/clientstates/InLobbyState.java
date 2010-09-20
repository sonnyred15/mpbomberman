/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InLobbyState extends AbstractClientState {
    private static final String STATE_NAME = "Lobby";

    private final Controller controller;
    private final Game lobby;

    public InLobbyState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.lobby = game;
    }

    @Override
    public ProtocolMessage<Integer, String> addBot(String botName) {
        //TODO
        return super.addBot(botName);
    }

    @Override
    public ProtocolMessage<Integer, String> addMessageToChat(String message) {
        //TODO
        return super.addMessageToChat(message);
    }

    @Override
    public ProtocolMessage<Integer, String> getGameInfo() {
        //TODO
        return super.getGameInfo();
    }

    @Override
    public ProtocolMessage<Integer, String> getNewMessagesFromChat() {
        //TODO
        return super.getNewMessagesFromChat();
    }

    @Override
    public ProtocolMessage<Integer, String> removeBot() {
        //TODO
        return super.removeBot();
    }

    @Override
    public ProtocolMessage<Integer, String> startGame() {
        //TODO
        return super.startGame();
    }

    @Override
    public ProtocolMessage<Integer, String> leave() {
        //TODO
        return super.leave();
    }

}
