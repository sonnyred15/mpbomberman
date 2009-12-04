package org.amse.bomberman.client.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.util.Constants.Direction;
/**
 * @author michail korovkin
 */
public class MapJFrameListener implements KeyListener{

    public void keyTyped(KeyEvent e) {
        System.out.println("keyTyped: " + e);
    }

    public void keyPressed(KeyEvent e) {
        IConnector connector = Model.getInstance().getConnector();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT : {
                connector.doMove(Direction.LEFT);
                break;
            }
            case KeyEvent.VK_UP: {
                connector.doMove(Direction.UP);
                break;
            }
            case KeyEvent.VK_RIGHT: {
                connector.doMove(Direction.RIGHT);
                break;
            }
            case KeyEvent.VK_DOWN: {
                connector.doMove(Direction.DOWN);
                break;
            }
            case KeyEvent.VK_SPACE: {
                connector.plantBomb();
                break;
            }
            default: return;
        }
    }

    public void keyReleased(KeyEvent e) {
        System.out.println("keyReleased: " + e);
    }

}
