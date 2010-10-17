package org.amse.bomberman.client.view.gamejframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.amse.bomberman.client.control.Controller;

/**
 * @author Mikhail Korovkin
 */
@SuppressWarnings("serial")
public class GameMenuBar extends JMenuBar {

    private JMenu game = new JMenu("Game");
    private JMenuItem leave = new JMenuItem();
    private JMenuItem exit = new JMenuItem("Exit");
    private final Controller controller;

    public GameMenuBar(final Controller controller) {
        this.controller = controller;

        game.add(leave);
        game.addSeparator();
        game.add(exit);
        leave.setAction(new LeaveAction());
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
//                GameModel.getInstance().removeListeners();
//                try {
                controller.disconnect();
//                } catch (NetException ex) {
//                    System.out.println(ex);
//                }
                System.exit(0);
            }
        });
        this.add(game);
    }

    @SuppressWarnings("serial")
    public class LeaveAction extends AbstractAction {

        public LeaveAction() {
            putValue(NAME, "Leave");
            putValue(SHORT_DESCRIPTION, "Leave this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            controller.requestLeaveGame();
        }
    }
}
