package org.amse.bomberman.client.view.gamejframe;

import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.NetException;
import org.amse.bomberman.client.control.impl.ControllerImpl;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;


/**
 * @author Mikhail Korovkin
 */
public class GameJMenuBar extends JMenuBar {

    private JMenu game = new JMenu("Game");
    private JMenuItem leave = new JMenuItem();
    private JMenuItem exit = new JMenuItem("Exit");

    public GameJMenuBar() {
        game.add(leave);
        game.addSeparator();
        game.add(exit);
        leave.setAction(new LeaveAction());
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Model.getInstance().removeListeners();
                try {
                    ControllerImpl.getInstance().requestLeaveGame();
                } catch (NetException ex) {
                    System.out.println(ex);
                }
                System.exit(0);
            }
        });
        this.add(game);
    }

    public static class LeaveAction extends AbstractAction {

        public LeaveAction() {
            putValue(NAME, "Leave");
            putValue(SHORT_DESCRIPTION, "Leave this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            try {
                ControllerImpl.getInstance().requestLeaveGame();
            } catch (NetException ex) {
                ControllerImpl.getInstance().lostConnection(ex.getMessage());
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