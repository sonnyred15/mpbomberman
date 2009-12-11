package org.amse.bomberman.client.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.client.net.IConnector;

/**
 * @author michail korovkin
 */
public class MapJMenuBar extends JMenuBar{
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
                 Model.getInstance().getConnector().leaveGame();
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
            Model.getInstance().getConnector().leaveGame();
            Model.getInstance().removeBots();
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
            connect.startGame();
            //this.setEnabled(false);
        }
    }
}
