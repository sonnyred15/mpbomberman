/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.renameme;

import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.Controller;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InGameState extends AbstractClientState {
    private static final String STATE_NAME = "Game";
    final Controller controller;
    final Game game;

    public InGameState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
    }

    @Override
    public ProtocolMessage<Integer, String> tryAddBot(String name) {
        return super.tryAddBot(name);
    }

    @Override
    public ProtocolMessage<Integer, String> addMessageToChat(String message) {
        return super.addMessageToChat(message);
    }

    @Override
    public ProtocolMessage<Integer, String> doMove(Direction dir) {
        return super.doMove(dir);
    }

    @Override
    public ProtocolMessage<Integer, String> getGameMapInfo(Game game) {
        return super.getGameMapInfo(game);
    }

    @Override
    public ProtocolMessage<Integer, String> getGamePlayersStats() {
        return super.getGamePlayersStats();
    }

    @Override
    public ProtocolMessage<Integer, String> getGameStatus() {
        return super.getGameStatus();
    }

    @Override
    public ProtocolMessage<Integer, String> getNewMessagesFromChat() {
        return super.getNewMessagesFromChat();
    }

    @Override
    public ProtocolMessage<Integer, String> leave() {
        return super.leave();
    }

    @Override
    public ProtocolMessage<Integer, String> placeBomb() {
        return super.placeBomb();
    }

    @Override
    public ProtocolMessage<Integer, String> tryRemoveBot() {
        return super.tryRemoveBot();
    }


}
