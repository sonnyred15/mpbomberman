/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

/**
 *
 * @author Kirilchuk V.E
 */
public enum CommandResult {
    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> 3)tryRemoveBot
     * <p> Tells that game was already started
     * and your operation is not success.
     */
    GAME_IS_ALREADY_STARTED(-1),

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> Tells that game is full
     * and your operation is not success.
     */
    GAME_IS_FULL(-3),

    /**
     * Possible return value of next methods:
     * <p> 1)tryAddBotIntoMyGame
     * <p> 2)tryRemoveBot
     * <p> Tells that you are not joined to any game so
     * you can not do this operation.
     */
    NOT_JOINED(-2),

    /**
     * Possible return value of next methods:
     * <p> 1)tryAddBotIntoMyGame
     * <p> 2)tryRemoveBot
     * <p> Tells that you are not owner of game so
     * you can not do this operation.
     */
    NOT_OWNER_OF_GAME(0),

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> Tells that you operation failed cause no such unstarted game.
     */
    NO_SUCH_UNSTARTED_GAME(-10),

    /**
     * Possible return value of next methods:
     * <p> 1)tryJoinGame
     * <p> 2)tryAddBotIntoMyGame
     * <p> 3)tryRemoveBot
     * <p> Tells that you operation was sucessful.
     */
    RESULT_SUCCESS(1),

    /**
     * Possible return value of next methods:
     * <p> tryRemoveBot
     * <p> Tells that there wasn`t bot to remove from game.
     */
    NO_SUCH_BOT(-5);

    //
    private int returnCode;

    private CommandResult(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return returnCode;
    }
}
