/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolConstants;
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
    private final Game game;
    private final int playerId;

    public InLobbyState(Controller controller, Game game, int playerId) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
        this.playerId = playerId;
    }

    @Override
    public ProtocolMessage<Integer, String> addBot(String botName) {
        CommandResult joinBotResult = this.tryAddBotIntoGame(botName);

        switch(joinBotResult) {
            case NOT_OWNER_OF_GAME: {
                // if not owner of game
                System.out.println("Session: addBot warning. "
                        + "Tryed to add bot to game, canceled. "
                        + "Not owner of the game.");
                return protocol.notOK2(
                        ProtocolConstants.ADD_BOT_MESSAGE_ID,
                        "Not owner of game.");
            }

            case GAME_IS_FULL: {
                System.out.println("Session: addBot warning. "
                        + "Tryed to add bot, canceled. Game is full.");
                return protocol.notOK2(
                        ProtocolConstants.ADD_BOT_MESSAGE_ID,
                        "Game is full. Try to add bot later.");
            }

            case GAME_IS_ALREADY_STARTED: {

                // if game.isStarted() true
                System.out.println("Session: addbot warning. "
                        + "Tryed to add bot to game ,canceled."
                        + " Game is already started.");
                return protocol.notOK2(
                        ProtocolConstants.ADD_BOT_MESSAGE_ID,
                        "Game was already started.");
            }

            case RESULT_SUCCESS: {
                System.out.println("Session: added bot to game."
                        + this.game.getName());
                return protocol.ok2(
                        ProtocolConstants.ADD_BOT_MESSAGE_ID,
                        "Bot added.");
            }

            default: {
                throw new AssertionError("Session: addbot error. Default block in switch "
                        + "statement. Error on server side.");
            }
        }
    }

    /**
     * Tryes to join bot into your game with specified nick name.
     * @param botName nick name of bot.
     * @return integer value that have next meanings
     * <p>
     * Controller.NOT_JOINED - if you are not joined to any game
     * and trying to add bot.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not join bot.
     * <p>
     * Controller.NOT_OWNER_OF_GAME - if you are not owner of the game
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join bot.
     * <p>
     * Controller.RESULT_SUCCESS - if bot was joined.
     */
    private CommandResult tryAddBotIntoGame(String botName) {
        CommandResult joinResult = CommandResult.NOT_OWNER_OF_GAME;

        if(this.game.isGameOwner(controller)) {
            synchronized(game) {
                joinResult = CommandResult.GAME_IS_FULL;
                if(!this.game.isFull()) {
                    joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                    if(!this.game.isStarted()) {
                        this.game.tryAddBot(controller, botName); //TODO swap args
                        joinResult = CommandResult.RESULT_SUCCESS;
                    }
                }
            }
        }
        return joinResult;
    }

    @Override
    public ProtocolMessage<Integer, String> removeBot() {
        //BIG TODO
        return super.removeBot();
    }

    @Override
    public ProtocolMessage<Integer, String> addMessageToChat(String message) {
        this.game.addMessageToChat(message);
        return protocol.ok2(ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                "Added.");
    }

    @Override
    public ProtocolMessage<Integer, String> getNewMessagesFromChat() {
        //TODO
        return super.getNewMessagesFromChat();
    }

    @Override
    public ProtocolMessage<Integer, String> getGameInfo() {
        return protocol.sendGameInfo2(game, controller);
    }

    @Override
    public ProtocolMessage<Integer, String> startGame() {
        if(game.isStarted()) {
            System.out.println("Session: sendMapArray warning. Canceled. Game is already started.");
            return protocol.notOK2(ProtocolConstants.START_GAME_MESSAGE_ID,
                                   "Game is already started.");
        }

        boolean success = game.tryStartGame(controller);

        if(success) {
            System.out.println("Session: started game. " + "(gameName="
                    + this.game.getName() + ")");
            for(Controller controller : game.getControllers()) {
                controller.setState(new InGameState(controller, game, playerId));//TODO BIG illegal players id.
            }

            return protocol.ok2(ProtocolConstants.START_GAME_MESSAGE_ID,
                                "Game started.");
        } else {
            System.out.println("Session: startGame warning. "
                    + "Client tryed to start game, canceled. "
                    + "Not an owner.");
            return protocol.ok2(ProtocolConstants.START_GAME_MESSAGE_ID,
                                "Not owner of game.");
        }
    }

    @Override
    public ProtocolMessage<Integer, String> getGameStatus() {
        return protocol.sendGameStatus2(game);
    }

    @Override
    public ProtocolMessage<Integer, String> leave() {
        //this.game.removeGameEndedListener(controller);//TODO BIG
        this.game.leaveFromGame(controller);
        this.game.leaveFromGame(controller);
        this.controller.setState(new NotJoinedState(controller));
        return protocol.ok2(ProtocolConstants.LEAVE_MESSAGE_ID,
                            "Disconnected.");
    }

}
