/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.renameme;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.gameinit.Lobby;
import org.amse.bomberman.server.net.tcpimpl.Controller;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InLobbyState extends AbstractClientState {
    private static final String STATE_NAME = "Lobby";

    private final Controller controller;
    private final Lobby lobby;

    public InLobbyState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.lobby = game.createLobby();
    }

    @Override
    public ProtocolMessage<Integer, String> tryAddBot(String botName) {
        //TODO
        lobby.tryAddBot(controller, botName);
        return super.tryAddBot(botName);
    }

    @Override
    public ProtocolMessage<Integer, String> addMessageToChat(String message) {
        //TODO
        lobby.addMessageToChat(controller, message);
        return super.addMessageToChat(message);
    }

    @Override
    public ProtocolMessage<Integer, String> getGameInfo() {
        //TODO
        lobby.getGameInfo();
        return super.getGameInfo();
    }

    @Override
    public ProtocolMessage<Integer, String> getNewMessagesFromChat() {
        //TODO
        lobby.getNewMessagesFromChat();
        return super.getNewMessagesFromChat();
    }

    @Override
    public ProtocolMessage<Integer, String> tryRemoveBot() {
        //TODO
        lobby.tryRemoveBot();
        return super.tryRemoveBot();
    }

    @Override
    public ProtocolMessage<Integer, String> tryStartGame() {
        //TODO
        lobby.tryStartGame(controller);
        return super.tryStartGame();
    }

    @Override
    public ProtocolMessage<Integer, String> leave() {
        //TODO
        lobby.leave(controller);
        return super.leave();
    }

}
