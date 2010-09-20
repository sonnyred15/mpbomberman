/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.net.tcpimpl.sessions.asynchro.controllers.clientstates;

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

    public InGameState(Controller controller, Game game) {
        super(STATE_NAME);
        this.controller = controller;
        this.game = game;
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

        boolean moved = this.game.tryDoMove(this.controller.getID(), dir);

        if(moved) {
            timer.setStartTime(System.currentTimeMillis());
        }

        return protocol.ok2(ProtocolConstants.DO_MOVE_MESSAGE_ID,
                            String.valueOf(moved));
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
    public ProtocolMessage<Integer, String> leave() {
        return super.leave();
    }

    @Override
    public ProtocolMessage<Integer, String> placeBomb() {
        return super.placeBomb();
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
