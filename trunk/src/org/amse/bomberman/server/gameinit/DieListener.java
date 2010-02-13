/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.EventListener;

/**
 *
 * @author Kirilchuk V.E
 */
public class DieListener implements EventListener {

    Game game;

    public DieListener(Game game) {
        this.game = game;
    }

    public void playerDied(Player player) {
        this.game.playerDied(player);
    }
}
