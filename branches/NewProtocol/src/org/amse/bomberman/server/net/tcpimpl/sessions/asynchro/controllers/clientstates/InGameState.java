/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import java.util.List;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InGameState extends AbstractClientState {

    private final MyTimer timer = new MyTimer(System.currentTimeMillis());
    private static final String STATE_NAME = "Game";
    final Controller controller;
    final Game game;
    final int playerId;

    public InGameState(Controller controller, Game game, int playerId) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
        this.playerId = playerId;
    }

    @Override
    public ProtocolMessage<Integer, String> doMove(Direction dir) {
        if(timer.getDiff() < Constants.GAME_STEP_TIME) {
            System.out.println("Session: doMove warning. "
                    + "Client tryed to move, canceled. "
                    + "Moves allowed only every "
                    + Constants.GAME_STEP_TIME + "ms.");

            return protocol.notOK2( //TODO it must not be ok =)
                    ProtocolConstants.DO_MOVE_MESSAGE_ID,
                    "false");
        }

        boolean moved = this.game.tryDoMove(playerId, dir);

        if(moved) {
            timer.setStartTime(System.currentTimeMillis());
        }

        return protocol.ok2(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                            String.valueOf(moved));
    }

    @Override
    public ProtocolMessage<Integer, String> getGameMapInfo() {
        if(!this.game.isStarted()) {
            
            return(protocol.notOK2(
                    ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID,
                    "Game is not started. You can`t get full game field info."));
        }

        List<String> data = protocol.getConverter().convertFieldExplPlayer(game, playerId);//TODO must be different commands...
        return(protocol.gameMapInfo2(data));
    }

    @Override
    public ProtocolMessage<Integer, String> getGamePlayersStats() {
        return protocol.sendPlayersStats2(game);
    }

    @Override
    public ProtocolMessage<Integer, String> getGameStatus() {
        return protocol.sendGameStatus2(game);
    }
    
    @Override
    public ProtocolMessage<Integer, String> getGameInfo() {
        return protocol.sendGameInfo2(game, controller);
    }

    @Override
    public ProtocolMessage<Integer, String> placeBomb() {
       this.game.tryPlaceBomb(playerId);
       return protocol.ok2(ProtocolConstants.PLACE_BOMB_MESSAGE_ID,
               "Placed.");
    }

    public ProtocolMessage<Integer, String> leave() {
        //this.game.removeGameEndedListener(controller); //TODO BIG
        this.game.leaveFromGame(controller);
        this.game.leaveFromGame(controller);
        this.controller.setState(new NotJoinedState(controller));
        return protocol.ok2(ProtocolConstants.LEAVE_MESSAGE_ID,
                "Disconnected.");
    }
    private class MyTimer {

        private long startTime;

        public MyTimer(long startTime) {
            this.startTime = startTime;
        }

        public long getDiff() {
            return System.currentTimeMillis() - this.startTime;
        }

        public void setStartTime(long time) {
            this.startTime = time;
        }

    }
}
