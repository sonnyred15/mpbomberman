package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameservice.bots.Bot;
import org.amse.bomberman.server.gameservice.bots.BotGamePlayer;
import org.amse.bomberman.server.gameservice.impl.Game;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.server.gameservice.impl.NetGamePlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InLobbyState extends AbstractClientState {

    private static final Logger LOG = LoggerFactory.getLogger(InLobbyState.class);
    
    private static final String STATE_NAME = "Lobby";
    private final Controller controller;
    private final Game game;

    public InLobbyState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
    }

    @Override
    public ProtocolMessage addBot(String botName) {
        CommandResult joinBotResult = this.tryAddBotIntoGame(botName);

        switch(joinBotResult) {
            case NOT_OWNER_OF_GAME: {
                // if not owner of game
                LOG.warn("Session: addBot warning. "
                        + "Tryed to add bot to game, canceled. "
                        + "Not owner of the game.");
                return protocol.notOk(
                        ProtocolConstants.BOT_ADD_MESSAGE_ID,
                        "Not owner of game.");
            }

            case GAME_IS_FULL: {
                LOG.warn("Session: addBot warning. "
                        + "Tryed to add bot, canceled. Game is full.");
                return protocol.notOk(
                        ProtocolConstants.BOT_ADD_MESSAGE_ID,
                        "Game is full. Try to add bot later.");
            }

            case GAME_IS_ALREADY_STARTED: {

                // if game.isStarted() true
                LOG.warn("Session: addbot warning. "
                        + "Tryed to add bot to game ,canceled."
                        + " Game is already started.");
                return protocol.notOk(
                        ProtocolConstants.BOT_ADD_MESSAGE_ID,
                        "Game was already started.");
            }

            case RESULT_SUCCESS: {
                LOG.info("Session: added bot to game."
                        + this.game.getGameName());
                return protocol.ok(
                        ProtocolConstants.BOT_ADD_MESSAGE_ID,
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

        if(this.game.isGameOwner(controller.getGamePlayer())) {
            synchronized(game) {
                joinResult = CommandResult.GAME_IS_FULL;
                if(!this.game.isFull()) {
                    joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                    if(!this.game.isStarted()) {                        
                        BotGamePlayer player = new BotGamePlayer();
                        player.setNickName(botName);
                        int playerId = game.tryJoin(player);
                        player.setPlayerId(playerId);
                        Bot bot = new Bot(player, game);
                        game.addGameChangeListener(bot);

                        joinResult = CommandResult.RESULT_SUCCESS;
                    }
                }
            }
        }
        return joinResult;
    }

    @Override
    public ProtocolMessage kickPlayer(int playerId) {
        boolean kicked = this.game.tryKickPlayer(controller.getGamePlayer(), playerId);
        if(kicked) {
            return protocol.ok(ProtocolConstants.KICK_PLAYER_MESSAGE_ID,
                    "Kicked.");
        } else {
            return protocol.notOk(ProtocolConstants.KICK_PLAYER_MESSAGE_ID,
                    "Not kicked.");
        }
    }

    @Override
    public ProtocolMessage addMessageToChat(String message) {
        this.game.addMessageToChat(controller.getGamePlayer().getNickName() +
                ": " + message);
        return protocol.ok(ProtocolConstants.CHAT_ADD_RESULT_MESSAGE_ID,
                "Added.");
    }

    @Override
    public ProtocolMessage getNewMessagesFromChat() {
        return protocol.chatMessage("No new messages.");
    }

    @Override
    public ProtocolMessage getGameInfo() {
        return protocol.gameInfo(game, controller.getGamePlayer());
    }

    @Override
    public ProtocolMessage startGame() {
        if(game.isStarted()) {
            System.out.println("Session: sendMapArray warning. Canceled. Game is already started.");
            return protocol.notOk(ProtocolConstants.START_GAME_MESSAGE_ID,
                                   "Game is already started.");
        }

        boolean success = game.tryStartGame(controller.getGamePlayer());

        if(success) {
            System.out.println("Session: started game. " + "(gameName="
                    + this.game.getGameName() + ")");
            return protocol.ok(ProtocolConstants.START_GAME_MESSAGE_ID,
                                "Game started.");
        } else {
            System.out.println("Session: startGame warning. "
                    + "Client tryed to start game, canceled. "
                    + "Not an owner.");
            return protocol.notOk(ProtocolConstants.START_GAME_MESSAGE_ID,
                                "Not owner of game.");
        }
    }

    @Override
    public ProtocolMessage getGameStatus() {
        return protocol.gameStatus(game);
    }

    @Override
    public ProtocolMessage leave() {
        NetGamePlayer gamePlayer = this.controller.getGamePlayer();
        this.game.removeGameChangeListener(gamePlayer);
        this.game.leaveFromGame(gamePlayer);
        gamePlayer.resetId();
        this.controller.setState(new NotJoinedState(controller));

        return protocol.ok(ProtocolConstants.LEAVE_MESSAGE_ID,
                            "Disconnected.");
    }
}
