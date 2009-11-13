package org.amse.bomberman.client.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.amse.bomberman.client.model.IModel;
import org.amse.bomberman.client.model.Map;
import org.amse.bomberman.client.model.Model;
/**
 *
 * @author michail korovkin
 */
public class MapJFrameListener implements KeyListener{

    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped: " + e);
    }

    public void keyPressed(KeyEvent e) {
        IModel model = Model.getInstance();
        // only for player with number 1 ???
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT : {
                model.movePlayer(1, Map.LEFT);
                break;
            }
            case KeyEvent.VK_UP: {
                model.movePlayer(1, Map.UP);
                break;
            }
            case KeyEvent.VK_RIGHT: {
                model.movePlayer(1, Map.RIGHT);
                break;
            }
            case KeyEvent.VK_DOWN: {
                model.movePlayer(1, Map.DOWN);
                break;
            }
            default: return;
        }
    }

    public void keyReleased(KeyEvent e) {
        System.out.println("keyReleased: " + e);
    }

}
