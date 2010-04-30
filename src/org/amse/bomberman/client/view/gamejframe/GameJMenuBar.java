package org.amse.bomberman.client.view.gamejframe;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.Controller;
import org.amse.bomberman.client.view.bomberwizard.BombWizard;

/**
 * @author Michail Korovkin
 */
public class GameJMenuBar extends JMenuBar {

    private GameJFrame parent = null;
    JMenu game = new JMenu("Game");
    JMenuItem leave = new JMenuItem();
    JMenuItem exit = new JMenuItem("Exit");

    public GameJMenuBar(GameJFrame frame) {
        parent = frame;
        game.add(leave);
        game.addSeparator();
        game.add(exit);
        leave.setAction(new LeaveAction(parent));
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Model.getInstance().removeListeners();
                try {
                    Controller.getInstance().requestLeaveGame();
                } catch (NetException ex) {
                    System.out.println(ex);
                }
                System.exit(0);
            }
        });
        this.add(game);
    }

    public static class LeaveAction extends AbstractAction {
        GameJFrame parent;

        public LeaveAction(GameJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Leave");
            putValue(SHORT_DESCRIPTION, "Leave this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                Controller.getInstance().requestLeaveGame();
            } catch (NetException ex) {
                Controller.getInstance().lostConnection(ex.getMessage());
                /*JOptionPane.showMessageDialog(parent,
                    ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                parent.dispose();
                Model.getInstance().setStart(false);
                Model.getInstance().removeListeners();
                BombWizard wizard = new BombWizard();
                Controller.getInstance().setReceiveInfoListener(wizard);
                wizard.setCurrentJPanel(BombWizard.IDENTIFIER1);*/
            }
        }
    }
}