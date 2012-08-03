package org.amse.bomberman.client.view.gamejframe;

import java.awt.event.KeyAdapter;
import org.amse.bomberman.client.control.Controller;
import org.amse.bomberman.util.Direction;
import java.awt.event.KeyEvent;

/**
 * @author Mikhail Korovkin
 * @author Kirilchuk V.E.
 */
public class GameKeyListener extends KeyAdapter {

    private final Controller controller;

    public GameKeyListener(Controller controller) {
        this.controller = controller;
    }

    @Override
    public void keyPressed(KeyEvent e) {
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
            default: {
                //ignore other keys
            }
        }
    }
}
