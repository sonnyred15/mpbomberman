package org.amse.bomberman.client.view.gamejframe;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.impl.Connector;
import org.amse.bomberman.client.net.NetException;
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
        IConnector connector = Connector.getInstance();
        try {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT: {
                    connector.doMove(Direction.LEFT);
                    //parent.tryScroll(Direction.LEFT);
                    break;
                }
                case KeyEvent.VK_UP: {
                    connector.doMove(Direction.UP);
                    //parent.tryScroll(Direction.UP);
                    break;
                }
                case KeyEvent.VK_RIGHT: {
                    connector.doMove(Direction.RIGHT);
                    //parent.tryScroll(Direction.RIGHT);
                    break;
                }
                case KeyEvent.VK_DOWN: {
                    connector.doMove(Direction.DOWN);
                    //parent.tryScroll(Direction.DOWN);
                    break;
                }
                case KeyEvent.VK_SPACE: {
                    connector.plantBomb();
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
