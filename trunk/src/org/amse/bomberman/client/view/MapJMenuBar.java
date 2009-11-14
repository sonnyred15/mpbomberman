package org.amse.bomberman.client.view;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;

/**
 * @author maverick
 */
public class MapJMenuBar extends JMenuBar{
     private MapJFrame parent = null;
     JMenu game = new JMenu("Game");
     JMenuItem leave = new JMenuItem("Leave");
     JMenuItem start = new JMenuItem("Start");

     public MapJMenuBar(MapJFrame frame) {
         parent = frame;
         game.add(start);
         game.add(leave);
         leave.setAction(new LeaveAction(parent));
         start.setAction(new StartAction(parent));
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
            Connector.getInstance().leaveGame();
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
            //----------------------------------------------------------------
            IConnector connect = Connector.getInstance();
            connect.startGame();
            //----------------------------------------------------------------
        }
    }
}
