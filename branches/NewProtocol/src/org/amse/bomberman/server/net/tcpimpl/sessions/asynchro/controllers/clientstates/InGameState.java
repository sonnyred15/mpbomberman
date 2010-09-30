/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

import java.util.List;
import org.amse.bomberman.protocol.ProtocolConstants;
import org.amse.bomberman.protocol.ProtocolMessage;
import org.amse.bomberman.server.gameservice.Game;
import org.amse.bomberman.server.gameservice.GamePlayer;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.Controller;
import org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.NetGamePlayer;
import org.amse.bomberman.util.Constants;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class InGameState extends AbstractClientState {

    private final MyTimer timer = new MyTimer(0);
    private static final String STATE_NAME = "Game";
    final Controller controller;
    final Game game;

    public InGameState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
    }

    @Override
    public ProtocolMessage<Integer, String> doMove(Direction direction) {
        if(timer.getDiff() < Constants.GAME_STEP_TIME) {
            System.out.println("Session: doMove warning. "
                    + "Client tryed to move, canceled. "
                    + "Moves allowed only every "
                    + Constants.GAME_STEP_TIME + "ms.");

            return protocol.notOk( //TODO it must not be ok =)
                    ProtocolConstants.DO_MOVE_MESSAGE_ID,
                    "false");
        }

        boolean moved = this.game.tryDoMove(controller
                .getGamePlayer().getPlayerId(), direction);

        if(moved) {
            timer.setStartTime(System.currentTimeMillis());
        }

        return protocol.ok(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                            String.valueOf(moved));
    }

    @Override
    public ProtocolMessage<Integer, String> getGameMapInfo() {
        if(!this.game.isStarted()) {
            
            return(protocol.notOk(
                    ProtocolConstants.GAME_MAP_INFO_MESSAGE_ID,
                    "Game is not started. You can`t get full game field info."));
        }

        List<String> data = protocol.getConverter().convertFieldExplPlayer(game,
                controller.getGamePlayer().getPlayerId());//TODO must be different commands...
        
        return(protocol.gameMapInfo(data));
    }

    @Override
    public ProtocolMessage<Integer, String> getGamePlayersStats() {
        return protocol.sendPlayersStats(game);
    }

    @Override
    public ProtocolMessage<Integer, String> getGameStatus() {
        return protocol.sendGameStatus(game);
    }
    
    @Override
    public ProtocolMessage<Integer, String> getGameInfo() {
        return protocol.sendGameInfo(game, controller.getGamePlayer());
    }

    @Override
    public ProtocolMessage<Integer, String> placeBomb() {
       this.game.tryPlaceBomb(controller.getGamePlayer().getPlayerId());
       return protocol.ok(ProtocolConstants.PLACE_BOMB_MESSAGE_ID,
               "Placed.");
    }

    @Override
    public ProtocolMessage<Integer, String> leave() {
        NetGamePlayer player = controller.getGamePlayer();
        this.game.removeGameChangeListener(player);
        this.game.leaveFromGame(player);
        this.controller.setState(new NotJoinedState(controller));
        player.resetId();
        return protocol.ok(ProtocolConstants.LEAVE_MESSAGE_ID,
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
