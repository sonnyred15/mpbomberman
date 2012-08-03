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

    private JMenu gameMenu = new JMenu("Game");

    private JMenuItem leave = new JMenuItem();
    private JMenuItem exit  = new JMenuItem("Exit");

    private final Controller controller;

    public GameMenuBar(final Controller controller) {
        this.controller = controller;

        gameMenu.add(leave);
        gameMenu.addSeparator();
        gameMenu.add(exit);
        leave.setAction(new LeaveAction());
        exit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                controller.disconnect();
                System.exit(0);
            }
        });

        add(gameMenu);
    }

    @SuppressWarnings("serial")
    public class LeaveAction extends AbstractAction {

        public LeaveAction() {
            putValue(NAME, "Leave");
            putValue(SHORT_DESCRIPTION, "Leave this game.");
            putValue(SMALL_ICON, null);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            controller.requestLeaveGame();
        }
    }
}
