package org.amse.bomberman.client.view.gamejframe;

import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.util.Constants.Direction;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Mikhail Korovkin
 */
public class GameJFrameListener implements KeyListener{

    public void keyTyped(KeyEvent e) {
    }

    public void keyPressed(KeyEvent e) {
        IController controller = Controller.getInstance();
        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: {
                    controller.requestDoMove(Direction.LEFT);
                    break;
                }
                case KeyEvent.VK_UP: {
                    controller.requestDoMove(Direction.UP);
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    controller.requestDoMove(Direction.RIGHT);
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    controller.requestDoMove(Direction.DOWN);
                    break;
                }
                case KeyEvent.VK_SPACE: {
                    controller.requestPlantBomb();
                    break;
                }
                default:
                    return;
            }
        } catch (NetException ex) {
            Controller.getInstance().lostConnection(ex.getMessage());
        }
    }

    public void keyReleased(KeyEvent e) {
    }

}
