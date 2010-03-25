/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.amse.bomberman.server.gameinit;

import java.util.EventListener;
import org.amse.bomberman.server.gameinit.imodel.IModel;

/**
 *
 * @author Kirilchuk V.E
 */
public class DieListener implements EventListener {
     //TODO IT MUST BE INTERFACE NOT NORMAL CLASS WITH REALIZATION!
    //CAUSE REALIZATION ALWAYS CHANGE DEPENDS ON WHERE THIS LISTENER IS!!!!
    IModel model;

    public DieListener(IModel model) {
        this.model = model;
}

    public void playerDied(Player player) {
        this.model.playerDied(player);
    }
}
