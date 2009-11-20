package org.amse.bomberman.client.view;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.amse.bomberman.client.model.BombMap;
import org.amse.bomberman.client.model.Model;
import org.amse.bomberman.client.net.Connector;
import org.amse.bomberman.client.net.IConnector;

/**
 *
 * @author michail korovkin
 */
public class ServerInfoJFrame extends JFrame{
    private final int width = 500;
    private final int height = 375;
    private JButton createJButton = new JButton();
    private JButton joinJButton = new JButton();
    private JButton refreshJButton = new JButton();
    private JTable table;
    private final Dimension buttonSize = new Dimension(200,40);

    
    public ServerInfoJFrame() {
        super("ServerInfo");
        setSize(width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(400, 150);
        setMinimumSize(new Dimension(width / 2, height / 2));

        /*createJButton.setPreferredSize(buttonSize);
        joinJButton.setPreferredSize(buttonSize);
        refreshJButton.setPreferredSize(buttonSize);
        createJButton.setMinimumSize(buttonSize);
        joinJButton.setMinimumSize(buttonSize);
        refreshJButton.setMinimumSize(buttonSize);
        */

        Box leftBox = Box.createVerticalBox();
        leftBox.add(createJButton);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(joinJButton);
        leftBox.add(Box.createVerticalStrut(10));
        leftBox.add(refreshJButton);

        this.refreshTable();
        Container c = getContentPane();
        c.setLayout(new FlowLayout());
        c.add(leftBox);
        c.add(table);

        refreshJButton.setAction(new RefreshAction(this));
        createJButton.setAction(new CreateAction(this));
        joinJButton.setAction(new JoinAction(this));
        setVisible(true);
    }
    private void refreshTable() {
        if (table == null) {
            String[] columnNames = {"ID", "Name", "Map", "Players", "maxPlayers"};
            // ???
            Object[][] data = new Object[15][5];
            table = new JTable(data, columnNames);
        }
        IConnector connect = Connector.getInstance();
        ArrayList<String> games = connect.takeGamesList();
        // if not "No games"
        if (games.get(0).charAt(0) != 'N') {
            int counter = 0;
            for (String game : games) {
                String[] buf = game.split(":");
                table.setValueAt(buf[0], counter, 0);
                table.setValueAt(buf[1], counter, 1);
                // only for early version
                table.setValueAt("1.map", counter, 2);
                table.setValueAt("?", counter, 3);
                table.setValueAt(4, counter, 4);
                counter++;
            }
        }
        table.repaint();
    }
    private void join() {
        if (table.getSelectedRow() != -1
                && table.getValueAt(table.getSelectedRow(), 0) != null) {
            int gameNumber = Integer.parseInt(
                    (String) table.getValueAt(table.getSelectedRow(), 0));
            IConnector connect = Connector.getInstance();
            connect.joinGame(gameNumber);
            //------------------------------------------------------------
            BombMap map = connect.getMap();
            Model model = (Model) Model.getInstance();
            MapJFrame frame = new MapJFrame(map);
            model.addListener(frame);
            model.setMap(map);
            connect.beginUpdating();
            //-------------------------------------------------------------
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "You did't select the game! "
                         + " Do this and then click join."
                        , "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static class RefreshAction extends AbstractAction {
        ServerInfoJFrame parent;

        public RefreshAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Refresh");
            putValue(SHORT_DESCRIPTION, "Refresh information from server");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.refreshTable();
        }
    }

    public static class CreateAction extends AbstractAction {
        ServerInfoJFrame parent;

        public CreateAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Create");
            putValue(SHORT_DESCRIPTION, "Create new Game");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            IConnector connect = Connector.getInstance();
            connect.createGame();
            parent.refreshTable();
        }
    }
    public static class JoinAction extends AbstractAction {
        ServerInfoJFrame parent;

        public JoinAction(ServerInfoJFrame jFrame) {
            parent = jFrame;
            putValue(NAME, "Join");
            putValue(SHORT_DESCRIPTION, "Join to the selected Game");
            putValue(SMALL_ICON, null);
        }
        public void actionPerformed(ActionEvent e) {
            parent.join();
        }
    }
}