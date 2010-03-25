/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.amse.bomberman.server.gameinit.bot;

import org.amse.bomberman.server.gameinit.imodel.IModel;
import org.amse.bomberman.util.Constants.Direction;

/**
 *
 * @author Kirilchuk V.E.
 */
public class MoveAction implements IAction{

    private Direction direction;
    private Bot bot;

    public MoveAction(Direction direction, Bot bot) {
        this.direction = direction;
        this.bot = bot;
    }

    public void executeAction(IModel model) {
        model.tryDoMove(this.bot, this.direction);
    }
}
