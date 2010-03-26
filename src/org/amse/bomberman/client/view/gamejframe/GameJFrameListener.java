package org.amse.bomberman.client.view.gamejframe;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.IController;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.util.Constants.Direction;
/**
 * @author Michail Korovkin
 */
public class GameJFrameListener implements KeyListener{
    private GameJFrame parent;
    public GameJFrameListener(GameJFrame jframe) {
        parent = jframe;
    }

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
            JOptionPane.showMessageDialog(parent, "Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void keyReleased(KeyEvent e) {
    }

}
