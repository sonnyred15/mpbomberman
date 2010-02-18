package org.amse.bomberman.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import org.amse.bomberman.client.model.impl.Model;
import org.amse.bomberman.client.net.IConnector;
import org.amse.bomberman.client.net.impl.Connector.NetException;

/**
 * @author Michail Korovkin
 */
public class MapJMenuBar extends JMenuBar {

    private MapJFrame parent = null;
    JMenu game = new JMenu("Game");
    JMenuItem leave = new JMenuItem();
    JMenuItem start = new JMenuItem();
    JMenuItem exit = new JMenuItem("Exit");

    public MapJMenuBar(MapJFrame frame) {
        parent = frame;
        game.add(start);
        game.add(leave);
        game.addSeparator();
        game.add(exit);
        leave.setAction(new LeaveAction(parent));
        start.setAction(new StartAction(parent));
        exit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                Model.getInstance().removeListener(parent);
                try {
                    Model.getInstance().getConnector().leaveGame();
                } catch (NetException ex) {
                    JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                System.exit(0);
            }
        });
        this.add(game);
    }

    public static class LeaveAction extends AbstractAction {
        MapJFrame parent;

        public LeaveAction(MapJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Leave");
            putValue(SHORT_DESCRIPTION, "Leave this game.");
            putValue(SMALL_ICON, null);
        }

        public void actionPerformed(ActionEvent e) {
            parent.dispose();
            Model.getInstance().removeListener(parent);
            try {
                Model.getInstance().getConnector().leaveGame();
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            Model.getInstance().removeBots();
            parent.stopWaitStart();
            ServerInfoJFrame serv = new ServerInfoJFrame();
        }
    }
    public static class StartAction extends AbstractAction {
        MapJFrame parent;

        public StartAction(MapJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Start");
            putValue(SHORT_DESCRIPTION, "Start this game.");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            IConnector connect = Model.getInstance().getConnector();
            try {
                connect.startGame();
                //this.setEnabled(false);
            } catch (NetException ex) {
                JOptionPane.showMessageDialog(parent,"Connection was lost.\n"
                    + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}