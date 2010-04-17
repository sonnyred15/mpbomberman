/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.bot;


import org.amse.bomberman.server.gameinit.Game;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class PlaceAndMoveAction implements IAction{

    private Direction direction;
    private Bot bot;

    public PlaceAndMoveAction(Direction direction, Bot bot) {
        this.direction = direction;
        this.bot = bot;
    }

    public void executeAction(Game game) {
        game.tryPlaceBomb(this.bot.getID());
        game.tryDoMove(this.bot.getID(), this.direction);
    }
}