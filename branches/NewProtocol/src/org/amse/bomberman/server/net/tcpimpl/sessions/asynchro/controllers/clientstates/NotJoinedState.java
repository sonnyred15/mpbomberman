/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import java.io.FileNotFoundException;
import java.io.IOException;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.util.Creator;

/**
 *
 * @author Kirilchuk V.E.
 */
public class NotJoinedState extends AbstractClientState {

    private static final String STATE_NAME = "Not Joined";
    private final Controller controller;

    public NotJoinedState(Controller controller) {
        super(STATE_NAME);
        this.controller = controller;
    }

    /**
     * Creates game. Add it to server. Setting session as owner of the game
     * and tryJoin owner into game.
     * <p>
     * If owner was in other game and tryes to create game
     * then this method will disconnect him from previous game, cause only one
     * game for client is supported.
     * <p>
     * Additionally setting this controller as GameEndedListener.
     * @see GameEndedListener
     * @see Game
     * @see GameMap
     * @param gameMapName name of gameMap to create.
     * @param gameName name of game to create.
     * @param maxPlayers maxPlayers parameter of game.
     * @throws FileNotFoundException if no gameMap with such name was finded.
     * @throws IOException if IO errors occurs while creating gameMap.
     */
    @Override
    public ProtocolMessage<Integer, String> createGame(String gameMapName,
                                                       String gameName,
                                                       int maxPlayers) {
        try {
            Game game = Creator.createGame(controller.getSession()
                    .getGameStorage(), controller, gameMapName, gameName, maxPlayers);

            game.addGameEndedListener(controller);

            controller.setState(new InLobbyState(controller, game));

            return protocol.ok2(ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                                "Game created.");
        } catch (FileNotFoundException ex) {
            System.out.println("Session: createGame warning. Client tryed to create game, canceled. "
                    + "Map wasn`t founded on server." + " Map=" + gameMapName);
            return protocol.notOK2(
                    ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                    "No such map on server.");
        } catch (IOException ex) {
            System.err.println("Session: createGame error while loadimg map. "
                    + " Map=" + gameMapName + " " + ex.getMessage());
            return protocol.notOK2(
                    ProtocolConstants.CREATE_GAME_MESSAGE_ID,
                    "Error on server side, while loading map.");
        }
    }

    @Override
    public ProtocolMessage<Integer, String> joinGame(int gameID) {
        CommandResult joinResult = this.tryJoinGame(gameID);

        switch(joinResult) {
            case NO_SUCH_UNSTARTED_GAME: {

                // if no unstarted gameParams with such gameID finded
                System.out.println("Session: client tryed to join gameID=" + gameID
                        + " ,canceled." + " No such game on server.");
                return (protocol.notOK2(
                        ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                        "No such game."));

            }

            case GAME_IS_ALREADY_STARTED: {

                // if gameParams with such gameID already started
                System.out.println("Session: joinGame warning. Client tryed to join gameID="
                        + gameID + " ,canceled."
                        + " Game is already started. ");
                return (protocol.notOK2(
                        ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                        "Game was already started."));
            }

            case GAME_IS_FULL: {

                System.out.println(
                        "Session: joinGame warning. Client tryed to join to full game, canceled.");
                return (protocol.notOK2(
                        ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                        "Game is full. Try to join later."));

            }

            case RESULT_SUCCESS: {

                System.out.println("Session: client joined to game." + " GameID="
                        + gameID + " Player=" + controller.getClientNickName());
                return (protocol.notOK2(
                        ProtocolConstants.JOIN_GAME_MESSAGE_ID,
                        "Joined."));
            }

            default: {
                throw new UnsupportedOperationException(
                        "Unknown result of join.");
            }
        }
    }

    /**
     * Tryes to join controller into game with specified nick name.
     * @param gameID ID of game to join in.
     * @param playerName nick name of player.
     * @return integer value that have next meanings
     * <p>
     * Controller.NO_SUCH_UNSTARTED_GAME - if there is no unstarted game
     * with such ID.
     * <p>
     * Controller.GAME_IS_ALREADY_STARTED - if game was already started
     * and you can not join.
     * <p>
     * Controller.GAME_IS_FULL - if game is full
     * and you can not join.
     * <p>
     * Controller.RESULT_SUCCESS - if you was joined.
     */
    private CommandResult tryJoinGame(int gameID) {

        Game gameToJoin = this.controller
                .getSession()
                .getGameStorage()
                .getGame(gameID);

        CommandResult joinResult = CommandResult.NO_SUCH_UNSTARTED_GAME;
        if(gameToJoin != null) {
            joinResult = CommandResult.GAME_IS_FULL;
            synchronized(gameToJoin) {
                if(!gameToJoin.isFull()) {
                    joinResult = CommandResult.GAME_IS_ALREADY_STARTED;

                    if(!gameToJoin.isStarted()) {
                        int playerID = gameToJoin.tryJoin(controller);
                        assert playerID > 0; //must be ok if synchronization is ok.

                        gameToJoin.addGameEndedListener(controller);
                        controller.setState(new InGameState(controller,
                                                            gameToJoin));
                        joinResult = CommandResult.RESULT_SUCCESS;
                    }
                }
            }
        }
        return joinResult;
    }

}
